package com.TwoPhaseMultiwayMergeSort;

import com.TwoPhaseMultiwayMergeSort.util.DeleteFilesInDirectory;
import com.TwoPhaseMultiwayMergeSort.util.getFilesListFromDirectory;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MergeSublistsPhaseTwo {

    protected int total_records = 0;

    private static final int MAX_TUPLES_IN_BLOCK=40;
//    private static int MAIN_MEMORY = 3; // 51 blocks -> 1 block = 40 tuples , then 51 blocks = 40*51 =2040 tuples

    protected int no_of_subLists = 0;

    static int iteration=1;

    static int writeCount =0;

    static int readCount =0;

    protected long mergeTime;

    public int mergeSortToOneFile(List<Path> listOfSubLists,File buffer) throws IOException {
        long itertionStart = System.currentTimeMillis();
        System.lineSeparator();
        String recordFromFile1 = null;
        String recordFromFile2 = null;
        File file1 = null;
        File file2 = null;
        int tuplesCount1 =0;
        int tuplesCount2 =0;
        int write = 0;


        for(int i=0;i<listOfSubLists.size();i=i+2) { //i=0 0,1    i=2 2,3 ...
            String currentMergedFile =System.getProperty("user.dir") +System.getProperty("file.separator")+"buffer" +System.getProperty("file.separator")+ iteration + "-sublist-" + i + "_" + (i+1);

            file1 = new File(listOfSubLists.get(i).toString()); //sublist1
            if((i+1)<listOfSubLists.size())
            {
                file2 = new File(listOfSubLists.get(i+1).toString()); //sublist2
            }
            else
            {
                file2 = null;
            }

            try {

                BufferedReader br1 = new BufferedReader(new FileReader(file1));
                BufferedReader br2=null;

                if(file2!=null )
                {
                    br2 = new BufferedReader(new FileReader(file2));
                }

                BufferedWriter bw = new BufferedWriter(new FileWriter(currentMergedFile));

                if(br1!=null && br2!=null)
                {
                    recordFromFile1 = br1.readLine();
                    recordFromFile2 = br2.readLine();
                    while (true) {
                        if(recordFromFile1==null){ //if no data left in file1 to mergex`
                            while(recordFromFile2 != null){
                                bw.write(recordFromFile2);
                                bw.newLine();
                                write++;
                                if(write == MAX_TUPLES_IN_BLOCK) {
                                    ++writeCount;
                                    write = 0;
                                }
                                recordFromFile2 = br2.readLine();
                            }
                            break;
                        }else if(recordFromFile2==null){ //if no data left in file2 to merge
                            while(recordFromFile1!= null){
                                bw.write(recordFromFile1);
                                bw.newLine();
                                write++;
                                if(write == MAX_TUPLES_IN_BLOCK) {
                                    ++writeCount;
                                    write = 0;
                                }
                                recordFromFile1 = br1.readLine();
                            }
                            break;
                        }
                        //if both records are equal
                        if (recordFromFile1.equals(recordFromFile2)) {
                            bw.write(recordFromFile1); //write first record
                            ++total_records; //to print total records
                            ++tuplesCount1;
                            bw.newLine();
                            write++;

                            bw.write(recordFromFile2); //write second record
                            ++total_records; //to print total records
                            ++tuplesCount2;
                            bw.newLine();
                            write++;


                            if(write == MAX_TUPLES_IN_BLOCK) {
                                ++writeCount;
                                write = 0;
                            }

                            recordFromFile1 = br1.readLine();
                            recordFromFile2 = br2.readLine();
                        } else if (recordFromFile1.compareTo(recordFromFile2) < 0)  //if record in file 1 is less than record in file2
                        {
                            bw.write(recordFromFile1);
                            bw.newLine();
                            write++;
                            if(write == MAX_TUPLES_IN_BLOCK) {
                                ++writeCount;
                                write = 0;
                            }

                            ++total_records; //to print total records
                            ++tuplesCount1;
                            recordFromFile1 = br1.readLine();
                        } else if (recordFromFile1.compareTo(recordFromFile2) > 0) //if record in file 1 is greater than record in file2
                        {
                            bw.write(recordFromFile2);
                            bw.newLine();
                            write++;
                            if(write == MAX_TUPLES_IN_BLOCK) {
                                ++writeCount;
                                write = 0;
                            }

                            ++total_records; //to print total records
                            ++tuplesCount2;
                            recordFromFile2 = br2.readLine();
                        }

                        if (tuplesCount1 == MAX_TUPLES_IN_BLOCK || tuplesCount2 == MAX_TUPLES_IN_BLOCK) {
                            ++readCount;
                            tuplesCount1 = 0;
                            ++readCount;
                            tuplesCount2= 0;
                        }
                    }
                    bw.close();
                    br1.close();
                    br2.close();
                    //delete processed subList
                    DeleteFilesInDirectory.deleteFile(buffer.getPath(), file1.getName());
                    DeleteFilesInDirectory.deleteFile(buffer.getPath(), file2.getName());
                }
                else if(br1!=null) {  //if only one subList left at the end
                    while ((recordFromFile1 = br1.readLine()) != null) {
                        bw.write(recordFromFile1);
                        bw.newLine();
                        write++;
                        if(write == MAX_TUPLES_IN_BLOCK) {
                            ++writeCount;
                            write = 0;
                        }

                        ++total_records; //to print total records
                        ++tuplesCount1;
                    }
                    bw.close();
                    br1.close();
                    //delete processed subList
                    DeleteFilesInDirectory.deleteFile(buffer.getPath(), file1.getName());
                }


            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        mergeTime  = mergeTime + (System.currentTimeMillis() - itertionStart);
        System.out.println(
                "Phase 2 merge time per iteration " + iteration + " : " + (System.currentTimeMillis() - itertionStart)
                        + "ms" + "(" + "~approx " + (System.currentTimeMillis() - itertionStart) / 1000.0 + "sec)");

        if (buffer.listFiles().length > 1) {
            iteration++;
            return mergeSortToOneFile(getFilesListFromDirectory.getFilesList(buffer.getPath()),buffer);
        } else {
            return buffer.listFiles().length;
        }
    }

}
