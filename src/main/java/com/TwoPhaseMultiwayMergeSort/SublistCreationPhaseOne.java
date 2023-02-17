package com.TwoPhaseMultiwayMergeSort;

import com.TwoPhaseMultiwayMergeSort.util.CopyDirectoryUtility;
import com.TwoPhaseMultiwayMergeSort.util.DeleteDirectory;
import com.TwoPhaseMultiwayMergeSort.util.getFilesListFromDirectory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class SublistCreationPhaseOne {

    private static final int MAX_TUPLES_IN_BLOCK=40;

    protected static int MAIN_MEMORY = 51; // 51 blocks -> 1 block = 40 tuples , then 51 blocks = 40*51 =2040 tuples

    protected int no_of_subLists = 0;

    protected long sort_time;

    static File buffer = null;

    String bufferLoc = System.getProperty("user.dir") + "/buffer";
//
//    static File buffer1 = null;
//
//    String buffer1Loc = System.getProperty("user.dir") + "/buffer1";

//    String buffer2Loc = System.getProperty("user.dir") + "/buffer2";

//    static File buffer2 = null;

    protected int total_records = 0;

    private void emptyBuffer() throws IOException {
        buffer = new File(bufferLoc); //to store subLists
//        buffer1 = new File(buffer1Loc); //to store subLists, so that it cannot be overwritten by another
//        buffer2 = new File(buffer2Loc);
        if (!buffer.exists()) {
            buffer.mkdir();
        }
        else if(buffer.isDirectory())
        {
            DeleteDirectory.deleteDir(String.valueOf(buffer.toPath()));
            buffer.mkdir();
        }
    }

    private List<String> sortFile(String fileLoc, String fileName) throws FileNotFoundException {

        String record = null;
        BufferedReader br = new BufferedReader(new FileReader(fileLoc));
        List<String> tempSubListsLoc = new ArrayList<String>();
        int j=0; //this is used to suffix subList number
        long beginTime = System.currentTimeMillis();

        int tuplesCount =0;

        do {
            try {
                List<String> subList = new ArrayList<String>(); //create new subList after reading data in 51 blocks

//                for (int i = 0; i < MAX_RECORDS; i++) {
                    while ((record = br.readLine()) != null) {
                        subList.add(record); //add a tuple or record in subList- 40.
                        ++total_records; //to print total records
                        ++tuplesCount;
                        if (tuplesCount == MAX_TUPLES_IN_BLOCK * MAIN_MEMORY) {
                            tuplesCount = 0;
                            break;
                        }
                    }

                            //sort the records based on whole string
                            Collections.sort(subList);

                            //subLists.add((ArrayList<String>) subList); // added sorted subList to subLists.

                            //add this sorted list to buffer
                            String outputFile = System.getProperty("user.dir") + "/buffer/sublist-"+fileName +"-" + ++j;
                            BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));
                            for(int i=0;i<subList.size();i++)
                            {
                                out.write(subList.get(i));
                                out.newLine();
                            }

                            out.close();
                            tempSubListsLoc.add(outputFile);

                } catch (IOException e) {
                throw new RuntimeException(e);
            }
            }while(record!=null);

        no_of_subLists = no_of_subLists + tempSubListsLoc.size();
        long endTime = System.currentTimeMillis();
        sort_time = sort_time + (endTime-beginTime);
        System.out.println("Time take to sort relation "+fileName+" is "+(endTime-beginTime)+"ms"+"("+"~approx "+(endTime-beginTime)/1000.0+"sec)");

        return tempSubListsLoc;

    }

    public static void main(String args[]) throws IOException {

        MergeSublistsPhaseTwo mergeSortPhaseTwo = new MergeSublistsPhaseTwo();

        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter file1 location:");
        String file1_loc = sc.nextLine();
        System.out.println("Please enter file2 location:");
        String file2_loc = sc.nextLine();
        SublistCreationPhaseOne spo = new SublistCreationPhaseOne();
        //sort the files and create sorted sublists (runs) & store it in buffer1 folder- content of file1 and buffer folder- content of file2
        if (file1_loc != null && file2_loc != null) {

            spo.emptyBuffer();
            System.out.println("--------------Phase 1 for Relation 1----------------");
            spo.sortFile(file1_loc, "R1"); // D:\Masters\COMP 6521- DB\Sample Project\r1.txt
            int blockCount1 = 0;
            int recordCountForR1 = spo.total_records;
            int numberOfSublistsForR1 = spo.no_of_subLists;
            if( recordCountForR1 % MAX_TUPLES_IN_BLOCK  == 0) {
                blockCount1 = recordCountForR1 / MAX_TUPLES_IN_BLOCK;
            } else {
                blockCount1 = (recordCountForR1 / MAX_TUPLES_IN_BLOCK) +1 ;

            }
            System.out.println("Records in  R1 : " + recordCountForR1);
            System.out.println("Number of subLists for R1 : " + numberOfSublistsForR1);
            System.out.println("Number of Blocks for R1 : " + blockCount1);

//            //transfer the content of buffer to buffer1 to avoid overwriting by second file subLists.
//            Path source = Paths.get(spo.bufferLoc);
//            Path target = Paths.get(spo.buffer1Loc);
//            CopyDirectoryUtility.copyDirectory(source, target);

            System.out.println("--------------Phase 1 for Relation 2----------------");
            spo.sortFile(file2_loc, "R2");
            int blockCount2 = 0;
            int recordCountForR2 = spo.total_records-recordCountForR1;
            if( recordCountForR2 % MAX_TUPLES_IN_BLOCK  == 0) {
                blockCount2 = recordCountForR2 / MAX_TUPLES_IN_BLOCK;
            } else {
                blockCount2 = (recordCountForR2 / MAX_TUPLES_IN_BLOCK) +1 ;

            }
            System.out.println("Records in  R2 : " + recordCountForR2);
            System.out.println("Number of subLists for R2 : " + (spo.no_of_subLists-numberOfSublistsForR1));
            System.out.println("Number of Blocks for R2 : " + blockCount2);

            System.out.println("Total number of records in R1 and R2: " + spo.total_records);
            System.out.println("Total Number of sublist " + spo.no_of_subLists);

            int diskIOPhaseOne = 2 * (blockCount1 + blockCount2);
            System.out.println("Total number of disk I/Os in phase1:  " + diskIOPhaseOne);
            System.out.println("Time take to sort relation R1 and R2 is " + spo.sort_time + "ms" + "(" + "~approx " + (spo.sort_time) / 1000.0 + "sec)");


        //merge the sorted sublists till we get 1 sorted file
        boolean run = true;
        while(run)
        {
            if(buffer.exists())
            {
                List<Path> subLists = null;

                subLists = getFilesListFromDirectory.getFilesList(buffer.getPath()); //get file list from buffer folder

                if(subLists.size()>1)
                {
                    System.out.println("--------------Phase 2-Merge----------------");
                    if(mergeSortPhaseTwo.mergeSortToOneFile(subLists,buffer)==1)
                        break;

                }

            }
        }


        System.out.println("Phase 2 Time : " + mergeSortPhaseTwo.mergeTime  + "ms" + " ("
                    + mergeSortPhaseTwo.mergeTime / 1000.0 + " sec)");

        int diskIOForPhaseTwo = mergeSortPhaseTwo.readCount + mergeSortPhaseTwo.writeCount;
        System.out.println("Merge Phase Disk I/Os :" + diskIOForPhaseTwo);

        System.out.println("--------------Point 3- assignment----------------");

        //read output file to count number of records
        Path filePath = getFilesListFromDirectory.getFilesList(buffer.getPath()).get(0);
        int recordCount = 0;
            try (BufferedReader br = new BufferedReader(new FileReader(filePath.toString()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    recordCount++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        System.out.println(
                    "Total Number of Records in Output File: " + recordCount);
        System.out.println(
                    "Total time Phase 1 & Phase 2 : " + (mergeSortPhaseTwo.mergeTime + spo.sort_time) + "ms");
        System.out.println("Total time Phase 1 & Phase 2 : "
                    + ((mergeSortPhaseTwo.mergeTime + spo.sort_time) / 1000.0) + " sec");
        System.out.println(
                    "Total Number of I/O : " + (diskIOPhaseOne + diskIOForPhaseTwo));

        }

    }
}
