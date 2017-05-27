package com.legach.apps.genkey;


import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;

public class engine extends MainActivity{

    public engine() {

    }

    public String generation(int len,String symbols)
    {
        String pass = "";//return this variable
        int n = 65;
        int[] abc = new int[62]; //for alphabet with ABC/abc/123
        Random rand = new Random();

        //заполняем массив алфавита всеми нужными знаками
        for (int i = 0; i < 62; i++)
        {
            abc[i] = n;
            n++;
            if (n == 91) n = 97;
            if (n == 123) n = 48;
        }
//int i1 = r.nextInt(max - min + 1) + min;
        switch (symbols) {
            case "AbC and 123":
                for (int j = 0; j < len; j++)
                {
                    n = rand.nextInt(62-0)+0;
                    pass += Character.toString((char)abc[n]);//Character.toChars(abc[n]);
                }
                break;
            case "abc and 123":
                for (int j = 0; j < len; j++)
                {
                    n = rand.nextInt(62-26)+26;
                    pass += Character.toString((char)abc[n]);
                }
                break;
            case "only AbC":
                for (int j = 0; j < len; j++)
                {
                    n = rand.nextInt(52-0)+0;
                    pass += Character.toString((char)abc[n]);
                }
                break;
            case "only abc":
                for (int j = 0; j < len; j++)
                {
                    n = rand.nextInt(52-26)+26;
                    pass += Character.toString((char)abc[n]);
                }
                break;
            case "only 123":
                for (int j = 0; j < len; j++)
                {
                    n = rand.nextInt(62-52)+52;
                    pass += Character.toString((char)abc[n]);
                }
                break;
        }
        return pass;
    }

    public String encode(String text, int _e, int n)//encoding text
    {
        String Str = "";
        int encLen;
        //System.Text.UTF8Encoding enc = new System.Text.UTF8Encoding();
        byte[] strBytes;
        try {
            strBytes = text.getBytes("utf-8");//"UTF8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            return "Not create bytes array";
        }
        for(byte value : strBytes)
        {
            int encrypt = XYmodN(value, _e, n); //c=m^e mod n
            if ((encLen = Integer.toString(encrypt).length()) < 5)
            {
                for (int i = 0; i < 5 - encLen; i++)
                    Str += "0";
            }
            Str += encrypt;// +"|";
        }
        return Str;
    }

    public String decode(String text, int _d, int n) //decoding text
    {
        String outStr = "";
        int[] arr = NumArray(text);
        byte[] bytes = new byte[arr.length];
        //System.Text.UTF8Encoding enc = new System.Text.UTF8Encoding();
        int j = 0;
        for(int value : arr)
        {
            byte decryptedValue = (byte)XYmodN(value, _d, n); //m= c^d mod n

            bytes[j] = decryptedValue;
            j++;

        }
        try {
            outStr = new String(bytes,"UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            return "Not create string from bytes";
        }
        //+= enc.GetString(bytes);
        return outStr;
    }

    private int[] NumArray(String text)
    {
        int i = 0;
        int[] result = new int[text.length()/ 5];
        char[] textChar = text.toCharArray();
        String tmp = "";
        int j = 0;
        for(char c : textChar)
        {
            tmp += Character.toString(c);
            j++;
            if (j == 5)//макимально пять символов в числе
            {
                result[i] = Integer.parseInt(tmp);
                tmp = "";
                j = 0;
                i++;
                continue;
            }
        }

        return result;
    }

    private int XYmodN(int value, int pw, int md)
    {
        // return (Int32)Math.Pow(value, pw) % md;
        int result = value;
        for (int i = 0; i < pw - 1; i++)
        {
            result = (result * value) % md;//если сразу возводить в степень, то возможен выход за рамки
        }
        return result;
    }

    /// Получить все варианты для e
    public ArrayList<Integer> eList(int ph)
    {
        ArrayList<Integer> result = new ArrayList<Integer>();
        for (int i = 2; i < ph; i++)
        {
            if (Euclide(i, ph).gcd == 1)
            {
                result.add(i);
            }
        }
        return result;
    }

    public class EuclideResult
    {
        EuclideResult(){

        }
        public int u1;
        public int u2;
        public int gcd;
    }

    //Euclide( result - secretkey d)
    public EuclideResult Euclide(int a, int b)//u1 * a + u2 * b = u3
    {
        int u1 = 1;
        int u3 = a;
        int v1 = 0;
        int v3 = b;

        while (v3 > 0)
        {
            int q0 = u3 / v3;
            int q1 = u3 % v3;

            int tmp = v1 * q0;
            int tn = u1 - tmp;
            u1 = v1;
            v1 = tn;

            u3 = v3;
            v3 = q1;
        }

        int tmp2 = u1 * (a);
        tmp2 = u3 - (tmp2);
        int res = tmp2 / (b);

        EuclideResult result = new EuclideResult();
        result.u1=u1;
        result.u2=res;
        result.gcd=u3;
        /*{
            u1 = u1,
            u2 = res,
            gcd = u3
        };*/
        return result;
    }

    //Массив простых чисел в границах x
    public Byte[] SimpleList()
    {
        ArrayList<Byte> noSimple = new ArrayList<Byte>();
        //последнее простое число тут 251,
        //следовательно макимсально возможное n = x*x = 63001, пять знаков
        //ushort, к слову, тоже пятизначный
        for (int x = 2; x < 256; x++)
        {
            int n = 0;
            for (int y = 1; y <= x; y++)
            {
                if (x % y == 0)
                    n++;
            }

            if (n <= 2)
                noSimple.add((byte)x);
        }
        Byte[] ByteArr = new Byte[noSimple.size()];

        return noSimple.toArray(ByteArr);
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


        String appendText = encode(text, PublicKey, newN) ;//Environment.NewLine;

        try {
            // отрываем поток для записи
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    openFileOutput(FileName, MODE_APPEND&MODE_PRIVATE)));
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
        //File.AppendAllText(path, appendText, Encoding.UTF8);

    }

    public ArrayList<String> read(String FileName, int PrivatKey, Context Activity)
    {
        ArrayList<String> items = new ArrayList<String>();
        // This text is added only once to the file.

        int newN = 0;
        String str = " ";
        try {
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    openFileInput(FileName)));

            // читаем содержимое
            // {
            String oldN = br.readLine();
            newN = Integer.parseInt(oldN);
            while ((str = br.readLine()) != null){
                items.add(decode(str,PrivatKey,newN));
                items.add(decode(str,PrivatKey,newN));
            }
            br.close();
            Toast.makeText(Activity, str.toString(), Toast.LENGTH_SHORT).show();
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

}
