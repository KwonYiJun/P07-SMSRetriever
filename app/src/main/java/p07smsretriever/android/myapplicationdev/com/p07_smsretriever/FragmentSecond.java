package p07smsretriever.android.myapplicationdev.com.p07_smsretriever;


import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSecond extends Fragment {


    Button btnRetrieve;
    TextView tvFrag2;
    TextView tvsms2;
    EditText etWord;

    public FragmentSecond() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);

        tvFrag2 = (TextView) view.findViewById(R.id.tvFrag2);
        tvsms2 = (TextView) view.findViewById(R.id.tvsms2);
        etWord = (EditText) view.findViewById(R.id.etword);
        btnRetrieve = (Button) view.findViewById(R.id.btnRetrieve2);

        btnRetrieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permissionCheck = PermissionChecker.checkSelfPermission
                        (getActivity(), Manifest.permission.READ_SMS);

                if (permissionCheck != PermissionChecker.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_SMS}, 0);
                    // stops the action from proceeding further as permission not
                    //  granted yet
                    return;
                }
                // Create all messages URI
                Uri uri = Uri.parse("content://sms");

                // The columns we want
                //  date is when the message took place
                //  address is the number of the other party
                //  body is the message content
                //  type 1 is received, type 2 sent
                String[] reqCols = new String[]{"date", "address", "body", "type"};

                //\\s = space, \\s+ can have more then one spaces
                String[] word = etWord.getText().toString().split("\\s+");
                // The filter String
                String filter = "body LIKE ?";
                // The matches for the ?
                for (int i=0; i < word.length; i++) {
                    word[i] = "%" + word[i] + "%";
                    if(i > 0) {
                        filter += " OR body Like ?";
                    }
                    Log.d("hi", word[i]);
                }
//                String[] filterArgs = {"%" + word + "%"};
                // Get Content Resolver object from which to
                //  query the content provider
                ContentResolver cr = getActivity().getContentResolver();
                // Fetch SMS Message from Built-in Content Provider
                Cursor cursor = cr.query(uri, reqCols, filter, word, null);
                String smsBody = "";
                if (cursor.moveToFirst()) {
                    do {
                        long dateInMillis = cursor.getLong(0);
                        String date = (String) DateFormat
                                .format("dd MMM yyyy h:mm:ss aa", dateInMillis);
                        String address = cursor.getString(1);
                        String body = cursor.getString(2);
                        String type = cursor.getString(3);
                        if (type.equalsIgnoreCase("1")) {
                            type = "Inbox:";
                        } else {
                            type = "Sent:";
                        }
                        smsBody += type + " " + address + "\n at " + date
                                + "\n\"" + body + "\"\n\n";
                    } while (cursor.moveToNext());
                }
                tvsms2.setText(smsBody);
            }
        });
        return view;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the read SMS
                    //  as if the btnRetrieve is clicked
                    btnRetrieve.performClick();

                } else {
                    // permission denied... notify user
                    Toast.makeText(getActivity(), "Permission not grant-ed",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}

