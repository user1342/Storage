package me.jamesstevenson.storage;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    public File currentDir = new File("");
    public File homeDir = new File(String.valueOf(Environment.getExternalStorageDirectory()));
    private Context mContext;
    // Get reference of widgets from XML layout



    public void viewDirectory(File directory){

        // globally
        TextView textView = (TextView)findViewById(R.id.textView2);
        textView.setText(directory.getPath());
        //test
        final ListView lv = (ListView) findViewById(R.id.lv);

        // Initializing a new String Array
        String[] listItems = new String[] {};



        // Create a List from String Array elements
        final List<String> dir_list = new ArrayList<String>(Arrays.asList(listItems));

        // Create an ArrayAdapter from List
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, dir_list);

        // DataBind ListView with items from ArrayAdapter
        lv.setAdapter(arrayAdapter);

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Requests permission

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);



        } else {
            //Peermission is set

            //Clears array
            arrayAdapter.clear();
            arrayAdapter.notifyDataSetChanged();

            //Reads files in the filepath and adds to list/ array
            File dir = new File(String.valueOf(directory));
            if (dir.exists()) {
                File[] files = dir.listFiles();
                for (int i = 0; i < files.length; ++i) {
                    File file = files[i];
                    dir_list.add(file.getName());
                    // do something here with the file
                }

                arrayAdapter.notifyDataSetChanged();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView tmplv = (ListView) findViewById(R.id.lv);

        currentDir = homeDir;
        viewDirectory(currentDir);



        FloatingActionButton home_fab = (FloatingActionButton) findViewById(R.id.home_fab);
        home_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentDir = homeDir;

                viewDirectory(currentDir);

            }
        });


        FloatingActionButton back_fab = (FloatingActionButton) findViewById(R.id.back_fab);
        back_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (currentDir == homeDir){
                    Snackbar.make(view, "You are in the home directory", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                }else {
                    currentDir = new File(currentDir.getParent());
                    try {
                        viewDirectory(currentDir);

                    }catch(NullPointerException e){
                        Log.v("TAG", String.valueOf(e));
                        Snackbar.make(view, "You are in the home directory", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        currentDir = homeDir;
                        viewDirectory(homeDir);
                        }

                }

            }
        });

        tmplv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final String selectedItem = (String) parent.getItemAtPosition(position);


                currentDir = new File(currentDir.getPath(), selectedItem);

                if (currentDir.isDirectory()) {

                    viewDirectory(currentDir);
                }else{

                    try {

                        String line = "";
                        StringBuilder text = new StringBuilder();

                        FileReader fReader = new FileReader(currentDir);
                        BufferedReader bReader = new BufferedReader(fReader);

                        try {

                            while( (line = bReader.readLine()) != null  ){
                                text.append(line+"\n");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        Context context = getApplicationContext();
                        int duration = Toast.LENGTH_LONG;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();





                    } catch (IOException e) {
                        e.printStackTrace();
                    }






                    currentDir = new File(currentDir.getParent());

                }
            }
        });


        tmplv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id){
                //Add alertDialog here

                final String selectedItem = (String) parent.getItemAtPosition(position);

                Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                File fileWithinMyDir = new File(currentDir.getPath() + "/" + selectedItem);
                if (fileWithinMyDir.isDirectory()){
                    Snackbar.make(view, "Can't share folder...", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();


                }else {
                    if (fileWithinMyDir.exists()) {
                        intentShareFile.setType("application/pdf");
                        intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + currentDir.getPath() + "/" + selectedItem));

                        intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                                "Sharing File...");
                        intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");

                        startActivity(Intent.createChooser(intentShareFile, "Share File"));
                    }
                }

                return false;
            }
        });

        FloatingActionButton update_fab = (FloatingActionButton) findViewById(R.id.update_fab);
        update_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Updating directory...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                viewDirectory(currentDir);

            }
        });

    }
}
