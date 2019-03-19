package com.example.nekretninefinalni.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nekretninefinalni.R;
import com.example.nekretninefinalni.adapters.DrawerAdapter;
import com.example.nekretninefinalni.db.DatabaseHelper;
import com.example.nekretninefinalni.db.model.Nekretnina;
import com.example.nekretninefinalni.db.model.Slike;
import com.example.nekretninefinalni.model.NavigationItems;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TOAST_MESSAGE = "toast";
    private static final int SELECT_PICTURE = 1;
    private static final int MY_PERMISSION_REQUEST_MEDINA = 1;

    private int position = 1;

    private Nekretnina nekretnina = null;
    private DatabaseHelper databaseHelper = null;

    private String imagePath = null;
    private ImageView preview = null;

    private ListView listViewMain = null;
    private List<Nekretnina> nekretninaList = null;
    private ArrayAdapter<Nekretnina> nekretninaArrayAdapter = null;

    private SharedPreferences sharedPreferences = null;
    private boolean showMessage = false;

    private Spannable message1 = null;
    private Spannable message2 = null;


    private DrawerLayout drawerLayout;
    private ListView drawerListView;
    private ActionBarDrawerToggle drawerToggle;
    private CharSequence drawerTitle;
    private RelativeLayout drawerPane;
    private ArrayList<NavigationItems> drawerItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationDrawer();

        prikaziListuNekretnina();

    }

    private void prikaziListuNekretnina() {
        listViewMain = findViewById(R.id.list_view_MAIN);
        try {
            nekretninaList = getDatabaseHelper().getNekretnina().queryForAll();
            nekretninaArrayAdapter = new ArrayAdapter<>(MainActivity.this, R.layout.list_array_adapter, R.id.list_array_text_view, nekretninaList);
            listViewMain.setAdapter(nekretninaArrayAdapter);
            listViewMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    nekretnina = (Nekretnina) listViewMain.getItemAtPosition(position);

                    Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                    intent.putExtra("id", nekretnina.getId());
                    startActivity(intent);

                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refresh() {
        listViewMain = findViewById(R.id.list_view_MAIN);
        if (listViewMain != null) {
            nekretninaArrayAdapter = (ArrayAdapter<Nekretnina>) listViewMain.getAdapter();
            if (nekretninaArrayAdapter != null) {
                try {
                    nekretninaList = getDatabaseHelper().getNekretnina().queryForAll();
                    nekretninaArrayAdapter.clear();
                    nekretninaArrayAdapter.addAll(nekretninaList);
                    nekretninaArrayAdapter.notifyDataSetChanged();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void dodajNekretninu() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dodaj_nekretninu);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Button gallery = dialog.findViewById(R.id.dodaj_nekretninu_button_gallery);
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSelfPermission();
                preview = dialog.findViewById(R.id.dodaj_nekretninu_preview);
                select_picture();
            }
        });

        final EditText editNaziv = dialog.findViewById(R.id.dodaj_nekretninu_naziv);
        final EditText editOpis = dialog.findViewById(R.id.dodaj_nekretninu_opis);
        final EditText editAdresa = dialog.findViewById(R.id.dodaj_nekretninu_adresa);
        final EditText editBrojTelefona = dialog.findViewById(R.id.dodaj_nekretninu_brojTelefona);
        final EditText editKvadratura = dialog.findViewById(R.id.dodaj_nekretninu_kvadratura);
        final EditText editBrojSobe = dialog.findViewById(R.id.dodaj_nekretninu_brojSobe);
        final EditText editCena = dialog.findViewById(R.id.dodaj_nekretninu_cena);

        Button confirm = dialog.findViewById(R.id.dodaj_nekretninu_button_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editNaziv.getText().toString().isEmpty()) {
                    editNaziv.setError("Polje naziv ne sme biti prazno!");
                    return;
                }
                if (editOpis.getText().toString().isEmpty()) {
                    editOpis.setError("Polje opis ne sme biti prazno!");
                    return;
                }
                if (editAdresa.getText().toString().isEmpty()) {
                    editAdresa.setError("Polje adresa ne sme biti prazno!");
                    return;
                }
                if (editBrojTelefona.getText().toString().isEmpty() || editBrojTelefona.getText().toString().length() < 5
                        || editBrojTelefona.getText().toString().length() > 10) {
                    editBrojTelefona.setError("Broj telefona mora biti duzi od 5 i manji od 10 !");
                    return;
                }
                if (editKvadratura.getText().toString().isEmpty()) {
                    editKvadratura.setError("Polje kvadratura ne sme biti prazno!");
                    return;
                }
                if (editBrojSobe.getText().toString().isEmpty()) {
                    editBrojSobe.setError("Polje broj sobe ne sme biti prazno!");
                    return;
                }
                if (editCena.getText().toString().isEmpty()) {
                    editCena.setError("Polje cena ne sme biti prazno!");
                    return;
                }
                if (imagePath == null || imagePath.isEmpty() || preview == null) {
                    Toast.makeText(MainActivity.this, "Morate odabrati sliku", Toast.LENGTH_LONG).show();
                    return;
                }

                String naziv = editNaziv.getText().toString();
                String opis = editOpis.getText().toString();
                String adresa = editAdresa.getText().toString();
                int brojTelefona = Integer.parseInt(editBrojTelefona.getText().toString());
                double kvadratura = Double.parseDouble(editKvadratura.getText().toString());
                int brojSobe = Integer.parseInt(editBrojSobe.getText().toString());
                double cena = Double.parseDouble(editCena.getText().toString());


                nekretnina = new Nekretnina();
                nekretnina.setNaziv(naziv);
                nekretnina.setOpis(opis);
                nekretnina.setAdresa(adresa);
                nekretnina.setBrojTelefona(brojTelefona);
                nekretnina.setKvadratura(kvadratura);
                nekretnina.setBrojSobe(brojSobe);
                nekretnina.setCena(cena);

                Slike slike = new Slike();
                slike.setSlike(imagePath);
                slike.setNekretnina(nekretnina);

                try {
                    getDatabaseHelper().getNekretnina().create(nekretnina);
                    getDatabaseHelper().getSlike().create(slike);
                    dialog.dismiss();
                    refresh();
                    resetImage();

                    message1 = new SpannableString("Uspesno kreirana Nekretnina sa nazivom: ");
                    message2 = new SpannableString(nekretnina.getNaziv());
                    message1.setSpan(new StyleSpan(Typeface.BOLD), 0, message1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    message2.setSpan(new ForegroundColorSpan(getColor(R.color.colorRED)), 0, message2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    if (showMessage) {
                        Toast toast = Toast.makeText(MainActivity.this, "", Toast.LENGTH_LONG);
                        View toastView = toast.getView();

                        TextView toastText = toastView.findViewById(android.R.id.message);
                        toastText.setText(message1);
                        toastText.append(message2);
                        toast.show();
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }


            }
        });

        Button cancel = dialog.findViewById(R.id.dodaj_nekretninu_button_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    /**
     * Navigaciona Fioka
     */
    private void navigationDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar_MAIN);
        setSupportActionBar(toolbar);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.show();
        }

        drawerItems.add(new NavigationItems("Nekretnine", "Prikazuje listu Nekretnina", R.drawable.ic_show_all));
        drawerItems.add(new NavigationItems("Podesavanja", "Otvara Podesavanja Aplikacije", R.drawable.ic_settings));

        DrawerAdapter drawerAdapter = new DrawerAdapter(this, drawerItems);
        drawerListView = findViewById(R.id.nav_list_MAIN);
        drawerListView.setAdapter(drawerAdapter);
        drawerListView.setOnItemClickListener(new DrawerItemClickListener());

        drawerTitle = getTitle();
        drawerLayout = findViewById(R.id.drawer_layout_MAIN);
        drawerPane = findViewById(R.id.drawer_pane_MAIN);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer) {

            @Override
            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(drawerTitle);
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                getSupportActionBar().setTitle(drawerTitle);
                super.onDrawerClosed(drawerView);
            }
        };

    }

    /**
     * OnItemClick iz NavigacioneFioke.
     */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position == 0) {
                refresh();
            } else if (position == 1) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }

            drawerLayout.closeDrawer(drawerPane);
        }
    }

    private void resetImage() {
        imagePath = "";
        preview = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_add_nekretnina:
                dodajNekretninu();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void consultPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        showMessage = sharedPreferences.getBoolean(TOAST_MESSAGE, true);
    }

    private void firstTimeDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.first_time_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Button ok = dialog.findViewById(R.id.first_time_button_OK);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    @Override
    protected void onResume() {
        refresh();
        consultPreferences();

        if (sharedPreferences.getBoolean("firstrun", true)) {
            firstTimeDialog();
            sharedPreferences.edit().putBoolean("firstrun", false).apply();
        }

        super.onResume();
    }


    public DatabaseHelper getDatabaseHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    @Override
    protected void onDestroy() {

        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }

        super.onDestroy();
    }

    private void select_picture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    private void checkSelfPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST_MEDINA);
        } else {
            // if not accepted, do something
        }

    }

    /**
     * <- Metoda za ucitavanje slike * Cuvanje putanje do slike. * ->
     */
    public String getImagePath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                    if (selectedImageUri != null) {
                        imagePath = getImagePath(selectedImageUri);
                    }
                    if (preview != null) {
                        preview.setImageBitmap(bitmap);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("position", position);
    }

}
