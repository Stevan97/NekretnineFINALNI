package com.example.nekretninefinalni.activities;

import android.Manifest;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
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
import android.text.style.UnderlineSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nekretninefinalni.R;
import com.example.nekretninefinalni.adapters.DrawerAdapter;
import com.example.nekretninefinalni.adapters.ImageViewAdapter;
import com.example.nekretninefinalni.db.DatabaseHelper;
import com.example.nekretninefinalni.db.model.Nekretnina;
import com.example.nekretninefinalni.db.model.Slike;
import com.example.nekretninefinalni.model.NavigationItems;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.ForeignCollection;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private static final String NOTIFY_MESSAGE = "notify";
    private static final String TOAST_MESSAGE = "toast";
    private static final int SELECT_PICTURE = 1;
    private static final int MY_PERMISSION_REQUEST_MEDINA = 1;

    private int position = 1;

    private Nekretnina nekretnina = null;
    private Slike slike = null;
    private DatabaseHelper databaseHelper = null;

    private String imagePath = null;
    private ImageView preview = null;

    private ForeignCollection<Slike> slikeForeignCollection = null;
    private List<Slike> slikeList = null;
    private GridView gridView = null;
    private ImageViewAdapter imageViewAdapter = null;

    private SharedPreferences sharedPreferences = null;
    private boolean showMessage = false;
    private boolean showNotify = false;

    private Spannable message1 = null;
    private Spannable message2 = null;
    private Toast toast = null;
    private View toastView = null;
    private TextView textToast = null;

    private Intent intentPosition = null;
    private int idPosition = 0;


    private DrawerLayout drawerLayout;
    private ListView drawerListView;
    private ActionBarDrawerToggle drawerToggle;
    private CharSequence drawerTitle;
    private RelativeLayout drawerPane;
    private ArrayList<NavigationItems> drawerItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        navigationDrawer();

        prikaziDetaljeNekretnine();

        onReservationButton(1);

    }

    private void prikaziDetaljeNekretnine() {
        intentPosition = getIntent();
        idPosition = intentPosition.getExtras().getInt("id");

        try {
            nekretnina = getDatabaseHelper().getNekretnina().queryForId(idPosition);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        TextView naziv = findViewById(R.id.detail_naziv_nekretnine);
        message1 = new SpannableString("Naziv Nekretnine: ");
        message2 = new SpannableString(nekretnina.getNaziv());
        spannableStyle();
        naziv.setText(message1);
        naziv.append(message2);

        TextView opis = findViewById(R.id.detail_opis);
        message1 = new SpannableString("Opis Nekretnine: ");
        message2 = new SpannableString(nekretnina.getOpis());
        spannableStyle();
        opis.setText(message1);
        opis.append(message2);

        message1 = new SpannableString("Adresa Nekretnine: ");
        message2 = new SpannableString(nekretnina.getAdresa());
        spannableStyle();
        final TextView adresa = findViewById(R.id.detail_adresa);
        adresa.setText(message1);
        message2.setSpan(new UnderlineSpan(), 0, message2.length(), 0);
        adresa.append(message2);
        adresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent geoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q="
                        + adresa.getText().toString()));
                startActivity(geoIntent);
            }
        });


        TextView brojTelefona = findViewById(R.id.detail_broj_telefona);
        message1 = new SpannableString("Broj Telefona Nekretnine: ");
        message2 = new SpannableString(String.valueOf(nekretnina.getBrojTelefona()));
        spannableStyle();
        brojTelefona.setText(message1);
        brojTelefona.append(message2);

        TextView kvadratura = findViewById(R.id.detail_kvadratura);
        message1 = new SpannableString("Kvadratura Nekretnine: ");
        message2 = new SpannableString(String.valueOf(nekretnina.getKvadratura()));
        spannableStyle();
        kvadratura.setText(message1);
        kvadratura.append(message2);

        TextView brojSobe = findViewById(R.id.detail_broj_sobe);
        message1 = new SpannableString("Broj sobe Nekretnine: ");
        message2 = new SpannableString(String.valueOf(nekretnina.getBrojSobe()));
        spannableStyle();
        brojSobe.setText(message1);
        brojSobe.append(message2);

        TextView cena = findViewById(R.id.detail_cena);
        message1 = new SpannableString("Cena Nekretnine: ");
        message2 = new SpannableString(String.valueOf(nekretnina.getCena()));
        spannableStyle();
        cena.setText(message1);
        cena.append(message2);

        gridView = findViewById(R.id.detail_LIST_IMAGE_VIEW);
        try {
            slikeForeignCollection = getDatabaseHelper().getNekretnina().queryForId(idPosition).getSlike();
            slikeList = new ArrayList<>(slikeForeignCollection);
            imageViewAdapter = new ImageViewAdapter(this, slikeList);
            gridView.setAdapter(imageViewAdapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    imagePath = String.valueOf(gridView.getItemAtPosition(position));
                    Dialog dialog = new Dialog(DetailActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                    dialog.setContentView(R.layout.full_screen_image);
                    dialog.show();

                    ImageView imageView = dialog.findViewById(R.id.full_screen_IMG);
                    imageView.setImageURI(Uri.parse(imagePath));

                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void izbrisiNekretninu() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dijalog_izbrisi);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        intentPosition = getIntent();
        idPosition = intentPosition.getExtras().getInt("id");

        TextView text = dialog.findViewById(R.id.izbrisi_nekretninu_text);
        message1 = new SpannableString("Da li ste sigurni da zelite da izbrisete Nekretninu pod nazivom: ");
        message2 = new SpannableString(nekretnina.getNaziv());
        spannableStyle();
        text.setText(message1);
        text.append(message2);

        Button confirm = dialog.findViewById(R.id.izbrisi_nekretninu_button_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    nekretnina = getDatabaseHelper().getNekretnina().queryForId(idPosition);
                    slikeForeignCollection = getDatabaseHelper().getNekretnina().queryForId(idPosition).getSlike();
                    slikeList = new ArrayList<>(slikeForeignCollection);
                    getDatabaseHelper().getSlike().delete(slikeList);
                    getDatabaseHelper().getNekretnina().delete(nekretnina);
                    onBackPressed();

                    message1 = new SpannableString("Uspesno Izbrisana Nekretnina: ");
                    message2 = new SpannableString(nekretnina.getNaziv());
                    spannableStyle();

                    if (showMessage) {
                        toast = Toast.makeText(DetailActivity.this, "", Toast.LENGTH_LONG);
                        toastView = toast.getView();

                        textToast = toastView.findViewById(android.R.id.message);
                        textToast.setText(message1);
                        textToast.append(message2);
                        toast.show();
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        Button cancel = dialog.findViewById(R.id.izbrisi_nekretninu_button_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


    }

    private void izmenaNekretnine() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.izmena_nekretnine);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        intentPosition = getIntent();
        idPosition = intentPosition.getExtras().getInt("id");

        Button gallery = dialog.findViewById(R.id.izmeni_nekretninu_button_gallery);
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog_izaberiSl = new Dialog(DetailActivity.this);
                dialog_izaberiSl.setContentView(R.layout.slika_za_izmenu);
                dialog_izaberiSl.setCanceledOnTouchOutside(false);
                dialog_izaberiSl.show();

                gridView = dialog_izaberiSl.findViewById(R.id.update_picture_gridView);
                try {
                    slikeForeignCollection = getDatabaseHelper().getNekretnina().queryForId(idPosition).getSlike();
                    slikeList = new ArrayList<>(slikeForeignCollection);
                    imageViewAdapter = new ImageViewAdapter(DetailActivity.this, slikeList);
                    gridView.setAdapter(imageViewAdapter);
                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            checkSelfPermission();
                            select_picture();
                            preview = dialog.findViewById(R.id.izmeni_nekretninu_preview);

                            slike = (Slike) gridView.getItemAtPosition(position);
                            dialog_izaberiSl.dismiss();
                        }
                    });
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        });

        final EditText editNaziv = dialog.findViewById(R.id.izmeni_nekretninu_naziv);
        final EditText editOpis = dialog.findViewById(R.id.izmeni_nekretninu_opis);
        final EditText editAdresa = dialog.findViewById(R.id.izmeni_nekretninu_adresa);
        final EditText editBrojTelefona = dialog.findViewById(R.id.izmeni_nekretninu_brojTelefona);
        final EditText editKvadratura = dialog.findViewById(R.id.zmeni_nekretninu_kvadratura);
        final EditText editBrojSobe = dialog.findViewById(R.id.izmeni_nekretninu_brojSobe);
        final EditText editCena = dialog.findViewById(R.id.izmeni_nekretninu_cena);

        Button confirm = dialog.findViewById(R.id.izmeni_nekretninu_button_confirm);
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
                    Toast.makeText(DetailActivity.this, "Morate odabrati sliku", Toast.LENGTH_LONG).show();
                    return;
                }

                String naziv = editNaziv.getText().toString();
                String opis = editOpis.getText().toString();
                String adresa = editAdresa.getText().toString();
                int brojTelefona = Integer.parseInt(editBrojTelefona.getText().toString());
                double kvadratura = Double.parseDouble(editKvadratura.getText().toString());
                int brojSobe = Integer.parseInt(editBrojSobe.getText().toString());
                double cena = Double.parseDouble(editCena.getText().toString());

                try {
                    nekretnina = getDatabaseHelper().getNekretnina().queryForId(idPosition);

                    nekretnina.setNaziv(naziv);
                    nekretnina.setOpis(opis);
                    nekretnina.setAdresa(adresa);
                    nekretnina.setBrojTelefona(brojTelefona);
                    nekretnina.setKvadratura(kvadratura);
                    nekretnina.setBrojSobe(brojSobe);
                    nekretnina.setCena(cena);

                    slike.setSlike(imagePath);
                    slike.setNekretnina(nekretnina);


                    getDatabaseHelper().getNekretnina().update(nekretnina);
                    getDatabaseHelper().getSlike().update(slike);
                    dialog.dismiss();
                    startActivity(getIntent());
                    finish();
                    overridePendingTransition(0, 0);
                    resetImage();

                    message1 = new SpannableString("Uspesna izmena | Novo ime Nekretnine:  ");
                    message2 = new SpannableString(nekretnina.getNaziv());
                    message1.setSpan(new StyleSpan(Typeface.BOLD), 0, message1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    message2.setSpan(new ForegroundColorSpan(getColor(R.color.colorRED)), 0, message2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    if (showMessage) {
                        Toast toast = Toast.makeText(DetailActivity.this, "", Toast.LENGTH_LONG);
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

        Button cancel = dialog.findViewById(R.id.izmeni_nekrenitnu_button_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void dodajSliku() {
        final Dialog dialog = new Dialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.add_photo);
        dialog.show();

        Button gallery = dialog.findViewById(R.id.add_photo_button_gallery);
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSelfPermission();
                preview = dialog.findViewById(R.id.add_photo_preview);
                select_picture();
            }
        });

        Button confirm = dialog.findViewById(R.id.add_photo_button_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentPosition = getIntent();
                idPosition = intentPosition.getExtras().getInt("id");

                try {

                    if (imagePath == null) {
                        Toast.makeText(DetailActivity.this, "Morate odabrati sliku", Toast.LENGTH_LONG).show();
                        return;
                    }

                    nekretnina = getDatabaseHelper().getNekretnina().queryForId(idPosition);

                    slike = new Slike();
                    slike.setSlike(imagePath);
                    slike.setNekretnina(nekretnina);
                    getDatabaseHelper().getSlike().create(slike);
                    resetImage();


                    if (showMessage) {
                        message1 = new SpannableString("Slika uspesno dodata u GridView!");
                        message1.setSpan(new ForegroundColorSpan(Color.RED), 0, message1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        Toast.makeText(DetailActivity.this, message1, Toast.LENGTH_LONG).show();
                    }

                    dialog.dismiss();
                    startActivity(getIntent());
                    finish();
                    overridePendingTransition(0, 0);

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        Button cancel = dialog.findViewById(R.id.add_photo_button_cancel);
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
        Toolbar toolbar = findViewById(R.id.toolbar_DETAIL);
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
        drawerListView = findViewById(R.id.nav_list_DETAIL);
        drawerListView.setAdapter(drawerAdapter);
        drawerListView.setOnItemClickListener(new DrawerItemClickListener());

        drawerTitle = getTitle();
        drawerLayout = findViewById(R.id.drawer_layout_DETAIL);
        drawerPane = findViewById(R.id.drawer_pane_DETAIL);

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
                onBackPressed();
            } else if (position == 1) {
                Intent intent = new Intent(DetailActivity.this, SettingsActivity.class);
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
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_detail_update:
                izmenaNekretnine();
                break;
            case R.id.menu_detail_add_photo:
                dodajSliku();
                break;
            case R.id.menu_detail_delete:
                izbrisiNekretninu();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void spannableStyle() {
        message1.setSpan(new StyleSpan(Typeface.BOLD), 0, message1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        message2.setSpan(new ForegroundColorSpan(getColor(R.color.colorRED)), 0, message2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void consultPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(DetailActivity.this);
        showMessage = sharedPreferences.getBoolean(TOAST_MESSAGE, true);
        showNotify = sharedPreferences.getBoolean(NOTIFY_MESSAGE, false);
    }


    @Override
    protected void onResume() {
        consultPreferences();

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

    private void onReservationButton(final int notificationID) {

        ImageButton reservationButton = findViewById(R.id.button_image_NOTIFY);
        reservationButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                NotificationChannel notificationChannel = new NotificationChannel("NOTIFY_ID", "ReserveNotify", NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setLightColor(Color.GREEN);

                NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                nm.createNotificationChannel(notificationChannel);

                if (showNotify) {
                    Notification notification = new Notification.Builder(DetailActivity.this)
                            .setContentTitle("Uspesno Zakazano Razgledanje!")
                            .setContentText("Za nekretninu: " + nekretnina.getNaziv())
                            .setSmallIcon(R.drawable.ic_notify)
                            .setChannelId("NOTIFY_ID")
                            .build();
                    nm.notify(notificationID, notification);
                }

            }
        });

    }

}
