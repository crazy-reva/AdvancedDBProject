package com.TwoPhaseMultiwayMergeSort;

import com.TwoPhaseMultiwayMergeSort.util.CopyDirectoryUtility;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class SublistCreationPhaseOne {

    private static final int MAX_RECORDS = 100000; //1 lac records
    private static final int MAX_TUPLES_IN_BLOCK=40;

    protected int no_of_subLists = 0;

    protected long sort_time;

    File buffer = null;

    String bufferLoc = System.getProperty("user.dir") + "/Two-Phase-Multiway-Merge-Sort/buffer";

    File buffer1 = null;

    String buffer1Loc = System.getProperty("user.dir") + "/Two-Phase-Multiway-Merge-Sort/buffer1";

    protected int total_records = 0;

    private void emptyBuffer()
    {
        buffer = new File(bufferLoc); //to store subLists
        buffer1 = new File(buffer1Loc); //to store subLists, so that it cannot be overwritten by another
        if (!buffer.exists() && !buffer1.exists()) {
            buffer.mkdir();
            buffer1.mkdir();
        }
        else if(buffer.isDirectory() && buffer1.isDirectory())
        {
            buffer.delete();
            buffer.mkdir();
            buffer1.delete();
            buffer1.mkdir();
        }
    }

    private List<ArrayList<String>> sortFile(String fileLoc, String fileName) throws FileNotFoundException {

        String record = null;
        BufferedReader br = new BufferedReader(new FileReader(fileLoc));
        List<ArrayList<String>> subLists = new ArrayList<ArrayList<String>>();
        int j=0; //this is used to suffix subList number
        long beginTime = System.currentTimeMillis();

        int tuplesCount =0;

        do {
            try {
                List<String> subList = new ArrayList<String>(); //create new subList for every block of size 40

                for (int i = 0; i < MAX_RECORDS; i++) {
                    if ((record = br.readLine()) != null) {
                        subList.add(record); //add a tuple or record in subList- 40.
                        ++total_records; //to print total records
                        ++tuplesCount;
                        if (tuplesCount == MAX_TUPLES_IN_BLOCK) {
                            tuplesCount = 0;

                            //sort the records in subList on studentID-primary key(will be unique for all ids)
                            Collections.sort(subList, new Comparator<String>() {
                                public int compare(String o1, String o2) {
                                    return o1.substring(0, 8).compareTo(o2.substring(0, 8));
                                }
                            });

                            subLists.add((ArrayList<String>) subList); // added sorted subList to subLists.

                            //add this sorted list to buffer
                            String outputFile = System.getProperty("user.dir") + "/buffer/sublist-" + ++j;
                            BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));
                            for (String s : subList) {
                                out.write(s);
                                out.newLine();
                            }

                            out.close();
                            subList.clear(); //clear subList to create new subList.
                        }
                    }
                }
            }
            catch(IOException e)
            {
                System.out.println(e);
            }
            }while(record!=null);

        no_of_subLists = no_of_subLists + subLists.size();
        long endTime = System.currentTimeMillis();
        sort_time = sort_time + (endTime-beginTime);
        System.out.println("Time take to sort relation "+fileName+" is "+(endTime-beginTime)+"ms"+"("+"~approx "+(endTime-beginTime)/1000.0+"sec)");

        return subLists;

    }

    public static void main(String args[]) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter file1 location:");
        String file1_loc = sc.nextLine();
        System.out.println("Please enter file2 location:");
        String file2_loc=sc.nextLine();

        if(file1_loc!=null && file2_loc!=null)
        {
                SublistCreationPhaseOne spo = new SublistCreationPhaseOne();
                spo.emptyBuffer();
                spo.sortFile(file1_loc,"R1"); // D:\Masters\COMP 6521- DB\Sample Project\r1.txt

                //transfer the content of buffer to buffer1 to avoid overwriting by second file subLists.
                Path source = Paths.get(spo.bufferLoc);
                Path target = Paths.get(spo.buffer1Loc);
                CopyDirectoryUtility.copyDirectory(source,target);

                spo.sortFile(file2_loc,"R2");

                System.out.println("Total number of records in R1 and R2: " + spo.total_records);
                System.out.println("Time take to sort relation R1 and R2 is "+spo.sort_time+"ms"+"("+"~approx "+(spo.sort_time)/1000.0+"sec)");
                System.out.println("Number of sublist "+ spo.no_of_subLists);
                System.out.println("Total number of blocks used to store the Tuples is "+(spo.total_records/MAX_TUPLES_IN_BLOCK));
        }
    }




}
