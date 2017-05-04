package com.example.bryan.ipcsharedatatestone.DataStorage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * contains all behavior related to working with saved files
 */

public class FileUtils {

    private static final String IMAGE_DIR = File.separator + "images";

    private File imageDirectory = null;


    public FileUtils(){

    }

    public void setupFileUtils(File root){
        if(imageDirectory==null)
        initImageDirectory(root);
    }

    public FileUtils(File root){
        initImageDirectory(root);
    }

    private void initImageDirectory(File root){
        imageDirectory = new File(root, IMAGE_DIR);
        if(!imageDirectory.exists())
            if(!imageDirectory.isDirectory())
                imageDirectory.mkdir();
    }

    public void saveImageToFile(byte[] data, String fileName) throws IOException{

        final File dataFile = new File(imageDirectory, fileName);
            dataFile.createNewFile();

        Log.i("test", "DATA FILE PATH: " + dataFile.getAbsolutePath());

        final FileOutputStream outputStream = new FileOutputStream(dataFile, false);

       // outputStream.write(data);
        //outputStream.close();
    }

    public void deleteImageFromFile(String fileName) throws FileNotFoundException, IOException{

        final File dataFile = new File(imageDirectory, fileName);
        if(dataFile.exists())
            dataFile.delete();


    }

    public Bitmap getFileData(String fileName)throws IOException{

        final File imageFile = new File(imageDirectory, fileName);

        final FileInputStream dataStream = new FileInputStream(imageFile);

        final Bitmap art = BitmapFactory.decodeStream(dataStream);

        return art;
    }


    public <Bitmap> void getDirImageData(){
        final File[] dataFiles = imageDirectory.listFiles();
        final List<Bitmap> dataList = new ArrayList<>();
    }



}
