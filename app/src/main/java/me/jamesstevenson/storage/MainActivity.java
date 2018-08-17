package me.jamesstevenson.storage;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import static android.provider.AlarmClock.EXTRA_MESSAGE;


public class MainActivity extends AppCompatActivity {

    public File currentDir = new File("");
    public File homeDir = new File(String.valueOf(Environment.getExternalStorageDirectory()));
    private Context mContext;
    // Get reference of widgets from XML layout

    // Used for the intent later
    public static final String EXTRA_MESSAGE = "me.jamesstevenson.storage.CreateFileActivity";


    /** Called when the user taps the Send button */
    // This opens the activity to the other activity in the app.
    public void createFile (View view) {
        Intent intent = new Intent(this, CreateFileActivity.class);
        intent.putExtra(EXTRA_MESSAGE, currentDir.toString());
        startActivity(intent);
    }

    public void viewDirectory(File directory){

        // globally
        TextView textView = (TextView)findViewById(R.id.textView2);
        textView.setText(directory.getPath());
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
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

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

        //This fab button opens anouther activity allowing the user to create .txt files.
        FloatingActionButton create_fab = (FloatingActionButton) findViewById(R.id.create_fab);
        create_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createFile(view);
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

                // A temp variable should be used here as it's never set back to current dir
                File tmpCurrentDir = currentDir;
                currentDir = new File(currentDir.getPath(), selectedItem);

                if (currentDir.isDirectory()) {

                    viewDirectory(currentDir);

                // Intents for opening images.
                }else if (selectedItem.endsWith(".jpg") || selectedItem.endsWith(".jpeg") || selectedItem.endsWith(".png")) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse("file://" + currentDir.getPath()), "image/*");
                    startActivity(intent);

                    /*Context context = getApplicationContext();
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, currentDir.getPath(), duration);
                    toast.show();*/


                    currentDir = new File(currentDir.getParent());
                // An intent that sends text to a text app or clipboard.
                }else if (selectedItem.endsWith(".txt")){
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

                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, (Serializable) text);
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);


                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    currentDir = new File(currentDir.getParent());

                }else{
                    Snackbar.make(view, "Not a valid file format.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    currentDir = new File(currentDir.getParent());

                }
            }
        });


        tmplv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id){

                final String selectedItem = (String) parent.getItemAtPosition(position);
                if (selectedItem.endsWith(".txt")) {
                    File data = new File(currentDir.getPath(), selectedItem);
                    data.delete();
                    Snackbar.make(view, "Deleted " + data.getName(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    viewDirectory(currentDir);
                }else{
                    Snackbar.make(view, "Can only delete .txt files.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
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
