package com.example.jameedean.nav2;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jameedean.nav2.adapter.NotesAdapter;
import com.example.jameedean.nav2.data.Reference;
import com.example.jameedean.nav2.model.NoteModel;
import com.example.jameedean.nav2.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class NavDrawerActivity extends MainActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NotesAdapter mAdapter;

    private final static int NOTE_ADD = 1000;

    private DatabaseReference mNotesReference;

    private ArrayList<String> mKeys;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private CircleImageView mDisplayImageView;
    private TextView mNameTextView;
    private TextView mEmailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mKeys = new ArrayList<>();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),NoteActivity.class);
                startActivityForResult(intent, NOTE_ADD);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View navHeaderView = navigationView.getHeaderView(0);

        mDisplayImageView = (CircleImageView) navHeaderView.findViewById(R.id.imageView_display);
        mNameTextView = (TextView) navHeaderView.findViewById(R.id.textView_name);
        mEmailTextView = (TextView) navHeaderView.findViewById(R.id.textView_email);

        FirebaseDatabase.getInstance().getReference(Reference.USER_KEY).child(mFirebaseUser.getEmail().replace(".", ","))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            Users users = dataSnapshot.getValue(Users.class);
                            Glide.with(NavDrawerActivity.this)
                                    .load(users.getPhotUrl())
                                    .into(mDisplayImageView);

                            mNameTextView.setText(users.getUser());
                            mEmailTextView.setText(users.getEmail());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_notes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new NotesAdapter(this, new NotesAdapter.OnItemClick() {
            @Override
            public void onClick(int pos) {
                // Open back note activity with data
                Intent intent = new Intent(getApplicationContext(), NoteActivity.class);
                intent.putExtra(Reference.NOTE_ID, mKeys.get(pos));
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(mAdapter);

        mNotesReference = FirebaseDatabase.getInstance().getReference(mFirebaseUser.getUid()).child(Reference.DB_NOTES);

}
    @Override
    protected void onStart() {
        super.onStart();

        // listening for changes
        mNotesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // clear table
                mKeys.clear();
                mAdapter.clear();
                // load data
                for (DataSnapshot noteSnapshot : dataSnapshot.getChildren()) {
                    NoteModel model = noteSnapshot.getValue(NoteModel.class);
                    mAdapter.addData(model);
                    mKeys.add(noteSnapshot.getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // stop listening
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
     public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
                mAuth.signOut();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            return false;
        }

        if (id == R.id.action_settings) {
            // launch settings activity
            Intent intent = new Intent(NavDrawerActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_settings_headers) {
            // launch settings activity
            Intent intent = new Intent(NavDrawerActivity.this, SettingsHeadersActivity.class);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_list) {
            // Handle the camera action
            Intent i = new Intent(this,NavDrawerActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_notepad) {
            Intent i = new Intent(this,AgencyMain_Activity.class);
            startActivity(i);

        } else if (id == R.id.nav_notification) {

        } else if (id == R.id.nav_manage) {
            Intent i = new Intent(this,PasswordApps_Activity.class);
            startActivity(i);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

