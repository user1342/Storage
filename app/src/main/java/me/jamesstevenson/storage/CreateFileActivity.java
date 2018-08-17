package me.jamesstevenson.storage;

import android.content.Intent;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CreateFileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //This makes it so that the keyboard doesn't show up automatically.
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        ;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_file);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        final String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        // Capture the layout's TextView and set the string as its text
        TextView textView = findViewById(R.id.textView);
        textView.setText(message);

        FloatingActionButton save_fad = (FloatingActionButton) findViewById(R.id.save_fad);
        save_fad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText inputTextTitle = (EditText) findViewById(R.id.editText);
                File newFile = new File(message + "/" + inputTextTitle.getText().toString()+ ".txt");
                if (!newFile.exists()) {
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

                }else{
                    Snackbar.make(view, "File "+ inputTextTitle.getText().toString()+ ".txt already exists.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });


    }


}
