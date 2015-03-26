package dk.arbr.tr.hamcam;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;


public class MainActivity extends ActionBarActivity {

    private static final String LOGTAG = "DebugTag";
    // TODO: Figure out what this request code means
    //       A request code is used when starting another activity that will return a result - the
    //       request code is passed back from the external activity to my app when onActivityResult is
    //       called so that I know who is calling onActivityResult.
    private static final int TAKE_PICTURE_REQUEST_CODE = 1;
    private Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Grab the button to add an event listener
        Button photoButton = (Button)findViewById(R.id.photoButton);
        photoButton.setOnClickListener(photoListener);
    }

    // TODO: Figure out what is going on here! Making a new instance with a new method?!
    //       Looks like a delegate to me.
    private View.OnClickListener photoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            takePhoto(v);
        }
    };

    private void takePhoto(View v) {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        // TODO: Make sure the device has a camera!
        //       Can be done in manifest: <uses-feature android:name="android.hardware.camera" android:required="true" />
        //       But this is only for filtering on Google Play, you still need runtime checking!
        // TODO: Make sure the emulator has an SD card! AVD -> Show advanced settings
        File photo = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "picture.jpeg");
        photoUri = Uri.fromFile(photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        startActivityForResult(intent, TAKE_PICTURE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // TODO: Figure out if this is necessary
        //       It may not be necessary, but it is good style:
        //       http://stackoverflow.com/questions/11745366/is-there-a-need-to-use-super-onactivityresult-in-onactivityresult
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == TAKE_PICTURE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // TODO: Figure out what a content resolver is
                //       A Content Provider manages access to a structured set of data. To access data in a
                //       Content Provider, we use a Content Resolver. But what does it do?
                getContentResolver().notifyChange(photoUri, null);

                ImageView photoView = (ImageView) findViewById(R.id.photoView);
                ContentResolver cr = getContentResolver();
                Bitmap bitmap;

                try {
                    // TODO: Remember to add <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
                    //       to the manifest file. Only the camera is actually using the write permission, so this
                    //       app only needs read permissions
                    bitmap = MediaStore.Images.Media.getBitmap(cr, photoUri);
                    // TODO: To resize bitmap, look at http://stackoverflow.com/questions/4837715/how-to-resize-a-bitmap-in-android
                    photoView.setImageBitmap(bitmap);
                } catch (Exception ex) {
                    Log.d(LOGTAG, ex.toString());
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // user clicked cancel
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

