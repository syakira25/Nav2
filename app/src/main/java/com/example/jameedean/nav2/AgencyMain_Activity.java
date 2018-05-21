package com.example.jameedean.nav2;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jameedean.nav2.adapter.AgencyAdapter;
import com.example.jameedean.nav2.data.Reference;
import com.example.jameedean.nav2.model.AgencyModel;
import com.example.jameedean.nav2.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AgencyMain_Activity extends MainActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private AgencyAdapter mAdapter;

    private final static int NOTE_ADD = 1000;

    private DatabaseReference mNotesReference1;

    private ArrayList<String> mKeys;

    private CircleImageView mDisplayImageView;
    private TextView mNameTextView;
    private TextView mEmailTextView;

    // Firebase Authentication
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mCurrentUser = mFirebaseAuth.getCurrentUser();

        setContentView(R.layout.activity_nav_drawer2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mKeys = new ArrayList<>();

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
                            Glide.with(AgencyMain_Activity.this)
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

        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AgencyActivity.class);
                startActivityForResult(intent, NOTE_ADD);
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_agency);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new AgencyAdapter(this, new AgencyAdapter.OnItemClick() {
            @Override
            public void onClick(int pos) {
                // Open back note activity with data
                Intent intent = new Intent(getApplicationContext(), AgencyActivity.class);
                intent.putExtra(Reference.AGENCY_ID, mKeys.get(pos));
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(mAdapter);

        mNotesReference1 = FirebaseDatabase.getInstance().getReference(mCurrentUser.getUid()).child(Reference.DB_AGENCY);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // listening for changes
        mNotesReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // clear table
                mKeys.clear();
                mAdapter.clear();
                // load data
                for (DataSnapshot noteSnapshot : dataSnapshot.getChildren()) {
                    AgencyModel model = noteSnapshot.getValue(AgencyModel.class);
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
        }else if(id == R.id.ic) {
            Intent i = new Intent(this, NoteActivity.class);
            startActivity(i);
        }else if (id == R.id.nav_notepad) {
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

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_agencymain, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_main:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.action_notes:
            Intent i = new Intent(this, NoteActivity.class);
            startActivity(i);
            finish();
            break;
        }

        return true;
    }*/

}

