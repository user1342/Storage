package me.jamesstevenson.storage;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;

public class CreateFileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //This makes it so that the keyboard doesn't show up automatically.
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)        ;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_file);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();

        // Sets the file name string as the filename passed by the intent. Blak if not present.
        // Then sets this to the title text box
        final String fileName = intent.getStringExtra(MainActivity.FileNAME);
        EditText inputTextTitle = (EditText) findViewById(R.id.editText);


        // Capture the layout's TextView and set the string as its text
        TextView textView = findViewById(R.id.textView);
        EditText editText2 = findViewById(R.id.editText2); //used for the content of the file

        // Gets the other part of the intent, the current directory.
        final String currentDir = intent.getStringExtra(MainActivity.CURRENT_DIR);
        textView.setText(currentDir);

        //Creates a file based off the current dir and filename.
        // As filename is blank if a file wasn't selected this won't make a file othewiside.
        File file = new File(currentDir+"/"+fileName);

        //Checks if the file is a real file.
        if (file.isFile()){
            // Removed the .txt from filenames so that they can be written over eachother.
            inputTextTitle.setText(fileName.replace(".txt",""));
            FileReader fReader = null;
            //Reads from the file
            try {
                fReader = new FileReader(file.getPath());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            BufferedReader bReader = new BufferedReader(fReader);
            StringBuilder text = new StringBuilder();

            try {
                String line = "";
                while( (line = bReader.readLine()) != null  ){
                    text.append(line+"\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            editText2.setText(text);


        }


        FloatingActionButton share_fab = (FloatingActionButton) findViewById(R.id.share_fab);
        share_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText2 = findViewById(R.id.editText2); //used for the content of the file

                Intent newsendIntent = new Intent();
                newsendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                newsendIntent.setAction(Intent.ACTION_SEND);
                newsendIntent.putExtra(Intent.EXTRA_TEXT, editText2.getText().toString());
                newsendIntent.setType("text/plain");
                startActivity(newsendIntent);

            }
            });





        FloatingActionButton save_fad = (FloatingActionButton) findViewById(R.id.save_fad);
        save_fad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText inputTextTitle = (EditText) findViewById(R.id.editText);
                File newFile = new File(currentDir + "/" + inputTextTitle.getText().toString()+ ".txt");

                    try {

                        newFile.createNewFile(); //Creates file with name of text.

                        try {
                            FileOutputStream fos = new FileOutputStream(newFile);
                            EditText inputText = (EditText) findViewById(R.id.editText2);
                            fos.write(inputText.getText().toString().getBytes());
                            Snackbar.make(view, "Creating "+ inputTextTitle.getText().toString()+ ".txt", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

            }
        });


    }


}
