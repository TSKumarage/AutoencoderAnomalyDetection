import data.reader.SampleFileParser;

import java.util.Scanner;

/**
 * Created by wso2123 on 10/12/16.
 */
public class Preprocessor {
    static String Name;

    public static void main(String [] args)
    {

        String inputFileName = "/home/wso2123/My Work/Datasets/KDD Cup/kddcup.data.corrected";
        String outputFileName = "/home/wso2123/My Work/Datasets/KDD Cup/kddcup.data.corrected_normal.csv";
        String lbl= "normal.";
        SampleFileParser parser= new SampleFileParser();
        parser.filter(inputFileName,outputFileName,lbl);


    }
}
