package com.legach.apps.genkey;

import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    //static engine e;

    public byte p;
    public byte q;
    public int F;
    public int n;//int32?
    public int _e;
    public int _d;
    public String NameOfFile;
    public int Length_n;
    private String currentFileName;
    public static String standartFileName = "key";
    private String dir;
    private int pos = 0;
    public static ClipboardManager clipmanager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //e = new engine();
        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //Ловушка для смены страницы
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // для каждой секции создается вкладка и её заголовк заполняется текстом
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        //Директория на карте памяти, где должны хранится пользовательсик файлы с ключами
        dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();

        //Объявление менеджера буфера обмена
        clipmanager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
    }



    //=====================Menu processing========================//
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //Выбор пункта
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(item.getItemId()) {
            case R.id.action_settings:
                onClickMenu_settings();
                break;
            case R.id.action_about:
                onClickMenu_about();
                break;
            case R.id.action_new:
                onClickMenu_newfile();
                break;
            case R.id.action_open:
                onClickMenu_openfile();
                break;
            case R.id.action_extract:
                onClickMenu_extractfile();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    //Описание функций при выборе элементов меню
    private void onClickMenu_extractfile() {
        //File src = new File(dir,fileName);
        File dst = new File(dir,standartFileName);
        for (int i=0;dst.exists();i++){
            dst = new File(dir,standartFileName+Integer.toString(i));
        }

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(dst));
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    openFileInput(standartFileName)));

            // Transfer bytes from in to out
            char[] buf = new char[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            in.close();
            out.close();
            Toast.makeText(this,"File has been created", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Toast.makeText(this,e.toString(), Toast.LENGTH_SHORT).show();
            return;
        }

    }

    public void onClickMenu_settings(){
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, ChangeSettings.class);
        Bundle b = new Bundle();
        intent.putExtras(b);
        //запускаем Intent
        startActivity(intent);
    }

    public void onClickMenu_about(){
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, ViewAbout.class);
        Bundle b = new Bundle();
        intent.putExtras(b);
        //запускаем Intent
        startActivity(intent);
    }

    public void onClickMenu_newfile() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.message_newDialog);

        builder.setPositiveButton(R.string.button_ok,
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id) {
                     TwoKey();
                    }
                });
        builder.setNegativeButton(R.string.button_cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.setCancelable(false);
        builder.show();
    }

    public void onClickMenu_openfile() {
    try {
        File folder = new File(dir);
        if(!folder.exists()){
            folder.mkdir();
        }

        final String[] files = findFiles(dir);
        if(files.length>0){
            pos = 0;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.title_openDialog);
            //Отображаем список файлов
            builder.setSingleChoiceItems(
                    files, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            pos = item;
                        }
                    }
            );

            //Обрабатываем закрытие с выбором
            builder.setPositiveButton(R.string.button_ok,
                    new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id) {
                            currentFileName = files[pos];
                            try {
                                openFile(currentFileName);
                            } catch (IOException e1) {
                                Toast.makeText(MainActivity.this,e1.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            //Обрабатываем закрытие без выбора
            builder.setNegativeButton(R.string.button_cancel,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            builder.setCancelable(false);
            builder.show();
        }
    }
    catch (Exception e) {
        Toast.makeText(this,e.toString(), Toast.LENGTH_SHORT).show();
    }
    }

    private void openFile(String fileName) throws IOException {
        File src = new File(dir,fileName);
        //File dst = new File(standartFileName);

        BufferedReader in = new BufferedReader(new FileReader(src));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                openFileOutput(standartFileName, MODE_PRIVATE)));;

        // Transfer bytes from in to out
        char[] buf = new char[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }

        in.close();
        out.close();
    }

    private String[] findFiles(String dirPath) {
        ArrayList<String> items = new ArrayList<String>();
        try{
            File f = new File(dirPath);
            File[] files = f.listFiles();
            for(int i = 0; i<files.length; i++){
                File file = files[i];
                if(!file.isDirectory()){
                    items.add(file.getName());
                }
            }
        }
        catch (Exception e) {
            Toast.makeText(this, e.toString(),Toast.LENGTH_SHORT).show();
        }

        return items.toArray(new String[items.size()]);
    }

    public void TwoKey() {

        engine e = new engine();
        Random random = new Random();
        Byte[] simple = e.SimpleList();
        p = simple[random.nextInt(simple.length)];
        q = simple[random.nextInt(simple.length)];
        n = (int)(p * q);
        F = (int)((p - 1) * (q - 1));

        if(n<0){
            n=65535-n;
        }
        if(F<0){
            F=65535-F;
        }

        ArrayList<Integer> possibleE = e.eList(F);//List нужен для .add
        int randtemp;
        do
        {
            randtemp = random.nextInt(possibleE.size());

            /*AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage(Integer.toString(randtemp) + "\n" + Integer.toString(possibleE.size()));
            builder1.setPositiveButton(R.string.button_ok,
                    new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            builder1.setCancelable(false);
            builder1.show();*/

            _e = possibleE.get(randtemp);//public key
            _d = e.Euclide(_e % F, F).u1;//privat key
        } while (_d < 0);
        //textBox3.Text = Convert.ToString(_e);
        //textBox4.Text = Convert.ToString(_d);
        try {
            // отрываем поток для записи
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    openFileOutput(standartFileName, MODE_PRIVATE)));
            // пишем данные
            bw.write(Integer.toString(n));
            bw.newLine();
            // закрываем поток
            bw.close();
            Toast.makeText(this,"File has been created", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException exc) {
            Toast.makeText(this,exc.toString(), Toast.LENGTH_SHORT).show();
        } catch (IOException exc) {
            Toast.makeText(this,exc.toString(), Toast.LENGTH_SHORT).show();
        }
        //n = (ushort)22339;
        //e = 16817;
        //d = 5153;
        //MessageBox.Show("Please, remember this!\nPublic key: " + Convert.ToString(_e) + "\nPrivate key: " + Convert.ToString(_d), "Attention!");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please, remember this!\nPublic key: " + Integer.toString(_e) + "\nPrivate key: " + Integer.toString(_d));

        builder.setPositiveButton(R.string.button_ok,
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.setCancelable(false);
        builder.show();

    }

    public void save(String FileName, String text, int PublicKey, Context Activity)//Save
    {
        int newN = 0;
        BufferedReader br;
        try {
            // открываем поток для чтения
            br = new BufferedReader(new InputStreamReader(
                    openFileInput(FileName)));
            //String str = "";
            // читаем содержимое
            //while ((str = br.readLine()) != null) {
        } catch (FileNotFoundException e) {
            Toast.makeText(Activity, "File does not exist! Create new file in New File" + e.toString(), Toast.LENGTH_SHORT).show();
            return;
        }
        String oldN = null;

        try {
            oldN = br.readLine();
            newN = Integer.parseInt(oldN);
            br.close();
        } catch (IOException e) {
            Toast.makeText(Activity, e.toString(), Toast.LENGTH_SHORT).show();
            return;
        }

        engine en = new engine();
        String appendText = en.encode(text, PublicKey, newN) ;//Environment.NewLine;

        try {
            // отрываем поток для записи
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    openFileOutput(FileName, MODE_APPEND)));
            // пишем данные
            bw.write(appendText);
            bw.newLine();
            // закрываем поток
            bw.close();
        } catch (FileNotFoundException e) {
            Toast.makeText(Activity, "File does not exist! Create new file in New File" + e.toString(), Toast.LENGTH_SHORT).show();
            return;
        } catch (IOException e) {
            Toast.makeText(Activity, e.toString(), Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(Activity, "Password has been added", Toast.LENGTH_SHORT).show();
        //File.AppendAllText(path, appendText, Encoding.UTF8);

    }

    public ArrayList<String> read(String FileName, int PrivatKey, Context Activity)
    {
        ArrayList<String> items = new ArrayList<String>();
        // This text is added only once to the file.

        int newN = 0;
        String str = "";
        engine en = new engine();
        try {
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    openFileInput(FileName)));

            // читаем содержимое
            // {
            String oldN = br.readLine();
            newN = Integer.parseInt(oldN);
            while ((str = br.readLine()) != null){
                items.add(en.decode(str, PrivatKey, newN));
            }
            br.close();
        } catch (FileNotFoundException e) {
            Toast.makeText(Activity, "File does not exist! Create new file in New File" + e.toString(), Toast.LENGTH_SHORT).show();
            items.add("Error file");
            return items;
        } catch (IOException e) {
            Toast.makeText(Activity, e.toString(), Toast.LENGTH_SHORT).show();
            items.add("Error file");
            return items;
        }
        return items;
    }

    //===========================Tab processing=======================//
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    //Адаптер для фрагментов
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment=null;
            Bundle args;
            switch(position){
                case 0:
                    fragment = new FirstFragment();
                    args = new Bundle();
                    args.putInt(FirstFragment.ARG_SECTION_NUMBER, position+1);
                    fragment.setArguments(args);
                    break;
                case 1:
                    fragment = new SecondFragment();
                    args = new Bundle();
                    args.putInt(SecondFragment.ARG_SECTION_NUMBER, position+1);
                    fragment.setArguments(args);
                    break;
            }
            return (Fragment)fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
            }
            return null;
        }
    }


    //===============First_fragment=================//
    public  class FirstFragment extends Fragment implements OnClickListener{
            private static final String ARG_SECTION_NUMBER = "section_number";
            //FirstFragment fragment = new FirstFragment();
            Bundle args = new Bundle();

        public FirstFragment() {
        }

        String SymbolsSet;
        SharedPreferences sp;
        String PassSize;
        int publickey;
        EditText editText_password, editText_comments, editText_pubkey;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_first, container, false);

            // получаем SharedPreferences, которое работает с файлом настроек
            sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
            // полная очистка настроек
            // sp.edit().clear().commit();

            final Button button_copy = (Button)rootView.findViewById(R.id.button_copy);
            final Button button_refresh = (Button)rootView.findViewById(R.id.button_refresh);
            final Button button_save = (Button)rootView.findViewById(R.id.button_save);

            button_copy.setOnClickListener((OnClickListener) this);
            button_refresh.setOnClickListener((OnClickListener) this);
            button_save.setOnClickListener((OnClickListener) this);

            editText_password = (EditText)rootView.findViewById(R.id.editText_password);
            editText_comments = (EditText)rootView.findViewById(R.id.editText_comments);
            editText_pubkey = (EditText)rootView.findViewById(R.id.editText_pubkey);

            return rootView;
        }

        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.button_copy:
                    onButtonCopyClick();
                    break;
                case R.id.button_refresh:
                    onButtonRefreshClick();
                    break;
                case R.id.button_save:
                    onButtonSaveClick();
                    break;
            }

        }
        //Копировать в буфер обмена
        private void onButtonCopyClick() {
            clipmanager.setText(editText_password.getText());
        }

        //Сохранить в файл, обозначенный ранее
        private void onButtonSaveClick() {
            if(editText_password.getText().toString().isEmpty()){
                Toast.makeText(getActivity(),"Password is Null", Toast.LENGTH_SHORT).show();
                return;
            }

            if(editText_pubkey.getText().toString().isEmpty()){
                Toast.makeText(getActivity(),"Please, enter the Public key", Toast.LENGTH_SHORT).show();
                return;
            }else {
                publickey = Integer.parseInt(editText_pubkey.getText().toString());
            }

            if(publickey<0){
                publickey=65535-publickey;
            }
            String comment;
            comment = editText_comments.getText().toString().replace('\n', ' ');
            String preEncodeText = editText_password.getText().toString() + " " + comment;

            //engine e = new engine();
            //e.
            save(standartFileName, preEncodeText, publickey, getActivity());
        }

        //Обновить пароль
        private void onButtonRefreshClick() {
            engine e = new engine();
            int PassSizeInt = Integer.parseInt(PassSize);
            if ((PassSizeInt<0)||(PassSizeInt>16))
                PassSizeInt=8;
            editText_password.setText(e.generation(PassSizeInt, SymbolsSet));
        }


        @Override
        public void onResume() {
            SymbolsSet = sp.getString("password_symbols", "nope");
            PassSize = sp.getString("password_size", "8");
            onButtonRefreshClick();
            super.onResume();
        }



    }

    //===============Second_fragment=================//
    public  class SecondFragment extends Fragment implements OnClickListener{
        private static final String ARG_SECTION_NUMBER = "section_number";
        //SecondFragment fragment = new SecondFragment();
        Bundle args = new Bundle();

        public SecondFragment() {
        }

        int privatkey;
        EditText editText_privatkey;
        ListView listView;
        ArrayList<String> readpass;
        ArrayAdapter<String> newAdapter;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_second, container, false);

            final Button button_read = (Button)rootView.findViewById(R.id.button_read);
            button_read.setOnClickListener((OnClickListener) this);

            editText_privatkey = (EditText)rootView.findViewById(R.id.editText_privkey);
            readpass = new ArrayList<String>();
            readpass.add("Not encode file");

            listView = (ListView)rootView.findViewById(R.id.listView_readpass);
            newAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1 , readpass);
            listView.setAdapter(newAdapter);

            listView.setOnItemClickListener(
                    new AdapterView.OnItemClickListener()
                    {
                        public void onItemClick(AdapterView <?> a, View v, int position, long id)
                        {

                        }
                    });
            return rootView;
        }

        //Обрабатываем щелчки на элементах ListView:



        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.button_read:
                    onButtonReadClick();
                    break;
            }

        }
        //Чтение из файла
        private void onButtonReadClick() {
            if(editText_privatkey.getText().toString().isEmpty()){
                Toast.makeText(getActivity(),"Please, enter the Privat key", Toast.LENGTH_SHORT).show();
                return;
            }else {
                privatkey = Integer.parseInt(editText_privatkey.getText().toString());
            }
            if(privatkey<0){
                privatkey=65535-privatkey;
            }

            //engine e = new engine();
            //readpass = e.read(standartFileName,privatkey,getActivity());
            readpass.clear();
            readpass=read(standartFileName,privatkey,getActivity());

            newAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, readpass);
            newAdapter.notifyDataSetChanged();
            listView.setAdapter(newAdapter);
        }


    }
}
