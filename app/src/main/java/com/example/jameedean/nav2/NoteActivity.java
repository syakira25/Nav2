package com.example.jameedean.nav2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jameedean.nav2.adapter.AgencyAdapter;
import com.example.jameedean.nav2.data.Reference;
import com.example.jameedean.nav2.model.NoteModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class NoteActivity extends AppCompatActivity {

    private EditText mTVTitle,mTVDescription;;
    private TextView mTVAgency,mItemSelected;
    private ImageView imageView, drawView;
    private ImageButton imageButton,drawButton;
    private Button  mOrder;
    private Spinner simpleSpinner;
    private String title, agency, description;

    Uri URI = null;
    private Uri mDrawingUri;
    private Uri mImageUri;
    private Paint paintDraw;

    private Bitmap bitmapMaster;
    private Canvas canvasMaster;

    ArrayList<Integer> mUserItems = new ArrayList<>();

    private static final int CAMERA_REQUEST_CODE = 10;
    private static final int SIGNATURE_REQUEST_CODE = 11;
    private DatabaseReference mNoteReference, mAgencyReference;
    private ArrayAdapter<String> mAgencyAdapter;
    private NoteModel mCurrentNoteModel;

    private Bitmap mCameraImg;
    private Bitmap mDrawImg;
    private boolean mHaveDrawing, mHaveImage;

    private String mId;
    private String[] listItems;
    boolean[] checkedItems;
    int prvX, prvY;

    // Firebase Authentication
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mCurrentUser;
    private FirebaseStorage mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mCurrentUser = mFirebaseAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();

        setContentView(R.layout.activity_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Binding
        mTVTitle = findViewById(R.id.et_title);
        mTVAgency = findViewById(R.id.et_agency);
        mItemSelected = findViewById(R.id.tvItemSelected);
        mTVDescription = findViewById(R.id.et_description);

        paintDraw = new Paint();
        paintDraw.setStyle(Paint.Style.FILL);
        paintDraw.setColor(Color.WHITE);
        paintDraw.setStrokeWidth(10);

        mOrder = (Button) findViewById(R.id.btnOrder);
        mItemSelected = (TextView) findViewById(R.id.tvItemSelected);

        listItems = getResources().getStringArray(R.array.category_item);
        checkedItems = new boolean[listItems.length];

        mOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(NoteActivity.this);
                mBuilder.setTitle(R.string.dialog_title);
                mBuilder.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
//                        if (isChecked) {
//                            if (!mUserItems.contains(position)) {
//                                mUserItems.add(position);
//                            }
//                        } else if (mUserItems.contains(position)) {
//                            mUserItems.remove(position);
//                        }
                        if (isChecked) {
                            mUserItems.add(position);
                        } else {
                            mUserItems.remove((Integer.valueOf(position)));
                        }
                    }
                });

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        String item = "";
                        for (int i = 0; i < mUserItems.size(); i++) {
                            item = item + listItems[mUserItems.get(i)];
                            if (i != mUserItems.size() - 1) {
                                item = item + ", ";
                            }
                        }
                        mItemSelected.setText(item);
                    }
                });

                mBuilder.setNegativeButton(R.string.dismiss_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                mBuilder.setNeutralButton(R.string.clear_all_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        for (int i = 0; i < checkedItems.length; i++) {
                            checkedItems[i] = false;
                            mUserItems.clear();
                            mItemSelected.setText("");
                        }
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });

        drawView = findViewById(R.id.iv_draw);
        drawView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NoteActivity.this, SignatureActivity.class);
                startActivityForResult(intent, SIGNATURE_REQUEST_CODE);
            }
        });

        imageButton = findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkForPermission(android.Manifest.permission.CAMERA) || !checkForPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) || !checkForPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    requestPermission(new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, CAMERA_REQUEST_CODE);
                } else {
                    takePicture();
                }

            }
        });
        imageView = findViewById(R.id.cameraImg);
        imageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int action = event.getAction();
                int x = (int) event.getX();
                int y = (int) event.getY();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        prvX = x;
                        prvY = y;
                        drawOnProjectedBitMap((ImageView) v, bitmapMaster, prvX, prvY, x, y);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        drawOnProjectedBitMap((ImageView) v, bitmapMaster, prvX, prvY, x, y);
                        prvX = x;
                        prvY = y;
                        break;
                    case MotionEvent.ACTION_UP:
                        drawOnProjectedBitMap((ImageView) v, bitmapMaster, prvX, prvY, x, y);
                        break;
                }
    /*
     * Return 'true' to indicate that the event have been consumed.
     * If auto-generated 'false', your code can detect ACTION_DOWN only,
     * cannot detect ACTION_MOVE and ACTION_UP.
     */
                return true;
            }

        });

        initializeSpinner();

        mNoteReference = FirebaseDatabase.getInstance().getReference(mCurrentUser.getUid()).child(Reference.DB_NOTES);

        Intent intent = getIntent();
        // Load record
        if (intent != null) {
            mId = intent.getStringExtra(Reference.NOTE_ID);
            if (mId != null) {
                mNoteReference.child(mId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mCurrentNoteModel = dataSnapshot.getValue(NoteModel.class);
                        if (mCurrentNoteModel != null) {
                            mTVTitle.setText(mCurrentNoteModel.getTitle());
                            mItemSelected.setText(mCurrentNoteModel.getCategory());
                            mTVDescription.setText(mCurrentNoteModel.getDescription());

                            // image
                            if (mCurrentNoteModel.getImageUrl() != null) {
                                Picasso.with(getApplicationContext()).load(mCurrentNoteModel.getImageUrl()).into(imageView);
                            }

                            // drawing
                            if (mCurrentNoteModel.getDrawingUrl() != null) {
                                Picasso.with(getApplicationContext()).load(mCurrentNoteModel.getDrawingUrl()).into(drawView);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
        simpleSpinner.setPrompt("Select an agency");
        mAgencyReference = FirebaseDatabase.getInstance().getReference(mCurrentUser.getUid()).child(Reference.DB_AGENCY);
        mAgencyAdapter = new ArrayAdapter<String>(NoteActivity.this, android.R.layout.simple_spinner_dropdown_item);
        simpleSpinner.setAdapter(mAgencyAdapter);

        // listening for changes
        mAgencyReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot noteSnapshot : dataSnapshot.getChildren()) {
                    String name = noteSnapshot.child("email").getValue(String.class);
                    mAgencyAdapter.add(name);
                }

                if (mCurrentNoteModel != null) {
                    if (!mCurrentNoteModel.getAgency().isEmpty()) {
                        int index = mAgencyAdapter.getPosition(mCurrentNoteModel.getAgency());
                        simpleSpinner.setSelection(index);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // stop listening
            }

        });
    }
     /*
    Project position on ImageView to position on Bitmap draw on it
     */

    private void drawOnProjectedBitMap(ImageView iv, Bitmap bm,
                                       float x0, float y0, float x, float y){
        if(x<0 || y<0 || x > iv.getWidth() || y > iv.getHeight()){
            //outside ImageView
            return;
        }else{

            float ratioWidth = (float)bm.getWidth()/(float)iv.getWidth();
            float ratioHeight = (float)bm.getHeight()/(float)iv.getHeight();

            canvasMaster.drawLine(
                    x0 * ratioWidth,
                    y0 * ratioHeight,
                    x * ratioWidth,
                    y * ratioHeight,
                    paintDraw);
            imageView.invalidate();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem item = menu.findItem(R.id.action_delete);

        if (mId == null) {
            item.setEnabled(false);
            item.setVisible(false);
        } else {
            item.setEnabled(true);
            item.setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_save:
                save();
                //sendEmail();
                break;
            case R.id.action_delete:
                if (!mId.isEmpty()) {
                    mNoteReference.child(mId).removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            actionNotification(databaseError, R.string.done_deleted);
                        }
                    });
                }
                break;
            case  R.id.action_send:
                save();
                sendEmail();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /***
     * Initialize agency into spinner list
     */
    private void initializeSpinner() {

        simpleSpinner = findViewById(R.id.simple_spinner);
        simpleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                mTVAgency.setText("Selected : " + selectedItemText);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void actionNotification(DatabaseError error, int successResourceId) {
        // close activity
        if (error == null) {
            Toast.makeText(NoteActivity.this, successResourceId, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(NoteActivity.this, error.getCode(), Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("LongLogTag")
    public void sendEmail() {
        try {
            title = mTVTitle.getText().toString();
            agency = simpleSpinner.getSelectedItem().toString();
            description = mTVDescription.getText().toString();
            final Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("image/png");
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{agency});
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, title);
            if (mImageUri != null) {
                emailIntent.putExtra(Intent.EXTRA_STREAM, mImageUri);
            }

            if (mDrawingUri != null) {
                emailIntent.putExtra(Intent.EXTRA_STREAM, mDrawingUri);
            }
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, description);
            this.startActivity(Intent.createChooser(emailIntent, "Sending email..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Request failed try again: ", Toast.LENGTH_LONG).show();
        }
        createNotification1();
    }

    private void takePicture() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, CAMERA_REQUEST_CODE);//zero can be replaced with any action code
    }

    private void requestPermission(final String[] permission, final int requestCode) {

        final Activity activity = this;
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(null)
                .setMessage("Allow Permissions For Camera and Storage")
                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Request permission
                        ActivityCompat.requestPermissions(activity,
                                permission,
                                requestCode);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length <= 0) {
            Log.i("PERMISSION REQUEST", "User interaction was cancelled.");
        } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            takePicture();
        } else {
            // Permission denied.
            corePermissionDenied();
        }
    }

    private void corePermissionDenied() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(null)
                .setMessage("Unable to get Permission")
                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Build intent that displays the App settings screen.
                        Intent intent = new Intent();
                        intent.setAction(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package",
                                BuildConfig.APPLICATION_ID, null);
                        intent.setData(uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }

    private boolean checkForPermission(String permission) {
        if (ActivityCompat.checkSelfPermission(getBaseContext(), permission) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case CAMERA_REQUEST_CODE:
                    getImageFromCamera(data);
                    mHaveImage = true;
                    break;
                case SIGNATURE_REQUEST_CODE:
                    byte[] bytes = data.getByteArrayExtra("sign_image");
                    mDrawImg = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    if (mDrawImg != null) {
                        drawView.setImageBitmap(mDrawImg);
                    }

                    mHaveDrawing = true;
                    break;

            }
        }
    }

    private void getImageFromCamera(Intent data) {
        mCameraImg = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        mCameraImg.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(), "photo.jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
            mImageUri = Uri.fromFile(destination);

            Bitmap.Config config;
            if(mCameraImg.getConfig() != null){
                config = mCameraImg.getConfig();
            }else{
                config = Bitmap.Config.ARGB_8888;
            }

            //bitmapMaster is Mutable bitmap
            bitmapMaster = Bitmap.createBitmap(
                    mCameraImg.getWidth(),
                    mCameraImg.getHeight(),
                    config);

            canvasMaster = new Canvas(bitmapMaster);
            canvasMaster.drawBitmap(mCameraImg, 0, 0, paintDraw
            );
            imageView.setImageBitmap(bitmapMaster);

        } catch (FileNotFoundException e) {
            Log.d("FileNotFoundException: ", e.getMessage());
        } catch (IOException e) {
            Log.d("IOException: ", e.getMessage());
        }
    }

    /***
     * Trigger save button
     */
    private void save() {

        if(mHaveDrawing || mHaveImage) {

            if (mHaveDrawing && mDrawImg != null) {
                uploadDrawing();
            }

            if (mHaveImage && bitmapMaster != null) {
                uploadImage();
            }

        } else {

            // If there is not image need to be uploaded,
            // skip the uploading process and jump to save record
            saveToDB();
        }
    }

    private void uploadDrawing() {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mDrawImg.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        OnSuccessListener successListener = new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mDrawingUri = taskSnapshot.getDownloadUrl();
                mHaveDrawing = false;
                saveToDB();
            }
        };

        OnFailureListener failureListener = new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        mStorage
                .getReference("reference")
                .child(System.currentTimeMillis() + ".jpg")
                .putBytes(data)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    private void uploadImage(){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmapMaster.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        OnSuccessListener successListener = new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mImageUri = taskSnapshot.getDownloadUrl();
                mHaveImage = false;
                saveToDB();

            }
        };
        OnFailureListener failureListener = new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        mStorage
                .getReference("reference")
                .child(System.currentTimeMillis() + ".jpg")
                .putBytes(data)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    /***
     * Save to Database
     */
    private void saveToDB() {

        // Image and drawing flag must be set to false before further by upload OnCompleteListener
        // If not return
        if(mHaveImage && mHaveDrawing) {
            return;
        }

        // Build note model
        NoteModel model;

        if(mCurrentNoteModel == null) {
            model = new NoteModel();
            model.setCreatedAt(System.currentTimeMillis());
        } else {
            model = mCurrentNoteModel;
        }

        model.setTitle(mTVTitle.getText().toString());
        model.setAgency(simpleSpinner.getSelectedItem().toString());
        model.setDescription(mTVDescription.getText().toString());
        model.setCategory(mItemSelected.getText().toString());

        if(mImageUri != null) {
            model.setImageUrl(mImageUri.toString());
        }

        if(mDrawingUri != null) {
            model.setDrawingUrl(mDrawingUri.toString());
        }

        if (mId == null) {
            // generate id
            mId = mNoteReference.push().getKey();
        }

        mNoteReference.child(mId)
                .setValue(model)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(NoteActivity.this, "Note saved", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    public void createNotification1() {
        // Prepare intent which is triggered if the
        // notification is selected
        Intent notification_intent = new Intent(this,NoteActivity.class);
//       PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notification_intent,0);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notification_intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Build notification
        // Actions are just fake
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setSmallIcon(R.drawable.ic_bell);
        notificationBuilder.setContentTitle("New Notification");
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setSound(defaultSoundUri);
        notificationBuilder.setContentIntent(pendingIntent);
        //       Notification noti = new Notification.Builder(this)
        //       .setContentTitle("New mail from " + "test@gmail.com")
//                .setContentText("Subject").setSmallIcon(R.drawable.ic_bell).build()
//                .addAction(R.drawable.ic_bell, "Call", pendingIntent)
//                .addAction(R.drawable.ic_bell, "More", pendingIntent)
//                .addAction(R.drawable.ic_bell, "And more", pendingIntent).build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//         hide the notification after its selected
//        notificationBuilder.flags |= Notification.FLAG_UPDATE_CURRENT;
//


        //notificationManager.notify(0, noti);

        //     NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,notificationBuilder.build());

    }

}

