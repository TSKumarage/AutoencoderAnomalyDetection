import core.impl.H2OServer;
import hex.deeplearning.DeepLearning;
import hex.deeplearning.DeepLearningModel;
import hex.deeplearning.DeepLearningParameters;
import hex.splitframe.ShuffleSplitFrame;
import water.Key;
import water.fvec.Frame;
import water.fvec.NFSFileVec;
import water.fvec.Vec;
import water.parser.ParseDataset;
import water.util.Log;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by wso2123 on 10/3/16.
 */
public class DeepLearningAutoencoderTestAlpha {

    static final String PATH = "/home/wso2123/My Work/Datasets/Breast cancer wisconsin/data.csv";
    static final String PATH2 = "/home/wso2123/My Work/Datasets/KDD Cup/kddcup.data.corrected";
    static final String PATH3 = "/home/wso2123/My Work/Datasets/Breast cancer wisconsin/validate.csv";
    static final String PATH4 = "/home/wso2123/My Work/Datasets/Breast cancer wisconsin/train.csv";
    static final String PATH5 = "/home/wso2123/My Work/Datasets/Breast cancer wisconsin/test.csv";

    public static void main(String[] args) {
        H2OServer.startH2O("54321");
        long seed = 0xDECAF;
        boolean isSaved=false;
        int data_size=0;
        Frame full_data=null,train = null, test = null, normal_data=null, validate=null;
        long tP=0,fP=0,tN=0,fN=0;
        Map<Float,Long> map_lbl=new HashMap<Float, Long>();
        try {

            if (!isSaved) {
                NFSFileVec nfs = NFSFileVec.make(find_test_file(PATH4));
                train = ParseDataset.parse(Key.make("train.hex"), nfs._key);
                nfs= NFSFileVec.make(find_test_file(PATH3));
                validate = ParseDataset.parse(Key.make("validate.hex"), nfs._key);
                nfs= NFSFileVec.make(find_test_file(PATH5));
                test = ParseDataset.parse(Key.make("test.hex"), nfs._key);

            }
            else {
                InputStream file = new FileInputStream("TrainSet.ser");
                InputStream buffer = new BufferedInputStream(file);
                ObjectInput input = new ObjectInputStream (buffer);
                train=new Frame((Frame) input.readObject());
                input.close();

                file = new FileInputStream("TestSet.ser");
                buffer = new BufferedInputStream(file);
                input = new ObjectInputStream (buffer);
                test=(Frame) input.readObject();
                input.close();
            }
            float sparsity_beta = 0.1f;
            StringBuilder sb = new StringBuilder();

            long size=train.numRows();
            float lbl;

            for (long i = 0; i < size; i++) {
                lbl = (float) train.vec("diagnosis").at(i);

                if (map_lbl.containsKey(lbl)) {
                    map_lbl.put(lbl, map_lbl.get(lbl) + 1);
                } else {
                    map_lbl.put(lbl, (long) 0);
                }
            }
            Iterator it = map_lbl.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                sb.append(pair.getKey()).append(" = ").append(pair.getValue()).append("\n");
                it.remove(); // avoids a ConcurrentModificationException
            }

            DeepLearningParameters p = new DeepLearningParameters();
            p._train = train._key;
            p._valid = validate._key;
            p._autoencoder = true;
            p._seed = seed;
            p._hidden = new int[]{9,9,9};
            p._l1 = 1e-4;
            p._activation = DeepLearningParameters.Activation.Tanh;
            p._epochs = 100;
            p._force_load_balance = true;
            DeepLearning dl = new DeepLearning(p);
            DeepLearningModel mymodel = dl.trainModel().get();

            Frame l2_frame_train = null, l2_frame_test = null;

            // Verification of results

            try {

                sb.append("Verifying results.\n");

                // Training data

                // Reconstruct data using the same helper functions and verify that self-reported MSE agrees
                double quantile = 0.60;

                l2_frame_train = mymodel.scoreAutoEncoder(train, Key.make(), false);
                final Vec l2_train = l2_frame_train.anyVec();
                size=l2_train.length();

                double thresh_test = l2_train.max();
                l2_frame_train.remove();

                l2_frame_test=mymodel.scoreAutoEncoder(test, Key.make(), false);

                Vec l2_test = l2_frame_test.anyVec();
//                double thresh_test  = mymodel.calcOutlierThreshold(l2_test, quantile);
                //thresh_test=0.0634;
                double mult = 5;
                //double thresh_test = mult * thresh_train;
                sb.append("\nFinding outliers.\n");
                sb.append("Mean reconstruction error (test): ").append(l2_test.mean()).append("\n");

                int out_count=0,no_count=0;
                // print stats and potential outliers

                sb.append("Quantile used: ").append(quantile*100).append("\n");
                sb.append("The following test points are reconstructed with an error greater than: ").append(thresh_test).append("\n");

                try {
                    sb.append("Size: ").append(l2_test.length()).append("\n");
                    sb.append("Size: ").append(test.numRows()).append("\n");
                    sb.append("Size: ").append(train.numRows()).append("\n");
                    size=l2_test.length();
                    map_lbl=new HashMap<Float, Long>();

                    for (long i = 0; i < size; i++) {
                        lbl= (float)test.vec("diagnosis").at(i);

                        if (map_lbl.containsKey(lbl))
                        {
                            map_lbl.put(lbl,map_lbl.get(lbl)+1);
                        }
                        else
                        {
                            map_lbl.put(lbl,(long)0);
                        }

                        if (l2_test.at(i) > thresh_test) {
                            out_count++;

                            if ((float)test.vec("diagnosis").at(i) != 0.0) {
                                tP++;
                            } else {
                                fP++;
                            }

                        } else {
                            no_count++;
                            if ((float)test.vec("diagnosis").at(i) != 0.0) {
                                fN++;
                            } else {
                                tN++;
                            }
                        }
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
                l2_frame_test.remove();

                it = map_lbl.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();
                    sb.append(pair.getKey()).append(" = ").append(pair.getValue()).append("\n");
                    it.remove(); // avoids a ConcurrentModificationException
                }
                /*Recall (sensitivity) - gives the true positive rate (TP / (TP + FN))
                Precision - gives the probability of predicting a true positive from all positive predictions (TP / (TP + FP))
                Precision recall (sensitivity) curve (PR curve) - plots precision vs. recall
                F1 score - gives the harmonic mean of precision and recall (sensitivity) (2TP / (2TP + FP + FN))
*/
                sb.append("Seed: ").append(seed).append("\n");
                sb.append("Outlier count: ").append(out_count).append("\n");
                sb.append("NOrmal count: ").append(no_count).append("\n");
                sb.append("Tp: ").append(tP).append("\n");
                sb.append("Fp: ").append(fP).append("\n");
                sb.append("Tn: ").append(tN).append("\n");
                sb.append("Fn: ").append(fN).append("\n");
                sb.append("Recall (sensitivity) true positive rate (TP / (TP + FN)) :").append(((double) tP / (tP + fN))*100).append("\n");
                sb.append("Precision (TP / (TP + FP) :").append(((double) tP / (tP + fP))*100).append("\n");
                sb.append(" F1 score (harmonic mean of precision and recall (sensitivity)) (2TP / (2TP + FP + FN)) :").append((2*(double)tP / (2*tP + fP + fN))*100).append("\n");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally {
                Log.info(sb);
                File file = new File("/home/wso2123/My Work/DeepLearner/src/main/resources/log_file.txt");
                BufferedWriter writer = null;
                try {
                    writer = new BufferedWriter(new FileWriter(file));
                    writer.append(sb);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (writer != null) try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                // cleanup
                if (mymodel != null) mymodel.delete();
                if (l2_frame_train != null) l2_frame_train.delete();
                if (l2_frame_test != null) l2_frame_test.delete();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
            if (train != null) train.delete();
            if (test != null) test.delete();
            H2OServer.stopH2O();
        }
    }

    protected static File find_test_file(String fname) {
        // When run from eclipse, the working directory is different.
        // Try pointing at another likely place
        File file = new File(fname);
        if (!file.exists())
            file = new File("target/" + fname);
        if (!file.exists())
            file = new File("../" + fname);
        if (!file.exists())
            file = new File("../../" + fname);
        if (!file.exists())
            file = new File("../target/" + fname);
        if (!file.exists())
            file = null;
        return file;
    }
}
