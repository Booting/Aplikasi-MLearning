package com.aplikasimlearning.activity;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.view.View;
import android.view.View.OnClickListener;
import com.aplikasimlearning.referensi.JSONParser;
import com.aplikasimlearning.referensi.Referensi;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

public class ModulActivity extends DashBoardActivity {
	private TextView txtGreetings;
	private Button btnUploadFile, btnBrowseFile;
	private String UserCategoriId = MainActivity.UserCategoriId;
	private String FullName = MainActivity.FullName;
	private JSONArray str_login = null;
	private ArrayList<HashMap<String, String>> angkatan = new ArrayList<HashMap<String, String>>();
	private ListAdapter adapter;
	private ProgressBar pb;
    private Dialog dialog;
    private int downloadedSize = 0;
    private int totalSize = 0;
    private TextView cur_val;
    private String dwnload_file_path = "", modulName = "";
    private String selectedImagePath;
	private static final int SELECT_PICTURE = 1;
	private ProgressDialog dialogg = null;
    private int serverResponseCode = 0;
    private EditText txtModulName;
    private String _ModulName, _ModulPath, ModulPath;
    private ProgressDialog pDialog;
    private String url;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_modul);
		setFooter(true, true);
		
		txtModulName  = (EditText) findViewById(R.id.txtModulName);
		btnUploadFile = (Button) findViewById(R.id.btnUploadFile);
		btnBrowseFile = (Button) findViewById(R.id.btnBrowseFile);
		txtGreetings  = (TextView) findViewById(R.id.txtGreetings);
		txtGreetings.setText("Welcome, " + FullName);
		
		if (UserCategoriId.equals("1")) {
			txtModulName.setVisibility(View.VISIBLE);
			btnUploadFile.setVisibility(View.VISIBLE);
			btnBrowseFile.setVisibility(View.VISIBLE);
		} else {
			txtModulName.setVisibility(View.GONE);
			btnUploadFile.setVisibility(View.GONE);
			btnBrowseFile.setVisibility(View.GONE);
		}
		
		showListModul();
		
		btnBrowseFile.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
		    	intent.setType("file/*");
		    	intent.setAction(Intent.ACTION_GET_CONTENT);
		    	startActivityForResult(
		    	Intent.createChooser(intent, "Select File"), SELECT_PICTURE);
			}
		});
		
		btnUploadFile.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (txtModulName.getText().toString().length()==0) {
					txtModulName.setError("Modul Name is Empty!!!");
				} else if (btnUploadFile.getText().toString().equals("Browse File..")) {
					btnUploadFile.setError("No file choosen!!!");
				} else {
					dialogg = ProgressDialog.show(ModulActivity.this, "", "Uploading file...", true);
					new Thread(new Runnable() {
		                 public void run() {
		                	 uploadFile(selectedImagePath, Referensi.url+"/UploadToServer.php");
		                 }
		            }).start(); 
				}
			}
		});
	}
	
	public void showListModul() {
		JSONParser jParser = new JSONParser();
        String link_url = Referensi.url+"/modul.php";
		JSONObject json = jParser.AmbilJson(link_url);

		try {
			angkatan.clear();
			str_login = json.getJSONArray("info");
			
			for(int i = 0; i < str_login.length(); i++){
				JSONObject ar = str_login.getJSONObject(i);

				String ModulPath = ar.getString("ModulPath");
				String ModulName = ar.getString("ModulName");
				String ModulId   = ar.getString("ModulId");
				
				HashMap<String, String> map = new HashMap<String, String>();

				map.put("ModulPath", ModulPath);
				map.put("ModulName", ModulName); 
				map.put("ModulId", ModulId);

				angkatan.add(map);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.adapter_listview();
	}

	public void adapter_listview() {
		adapter = new SimpleAdapter(this, angkatan, R.layout.list_item, new String[] { "ModulPath", "ModulName", "ModulId"}, new int[] {R.id.txtModulPath, R.id.txtModulName, R.id.txtModulId});

		setListAdapter(adapter);
		ListView lv = getListView();
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				modulName		  = ((TextView) view.findViewById(R.id.txtModulName)).getText().toString();
				dwnload_file_path = ((TextView) view.findViewById(R.id.txtModulPath)).getText().toString();
				showProgress(dwnload_file_path);
		        new Thread(new Runnable() {
		            public void run() {
		            	 downloadFile();
		            }
		          }).start();
			}
		});
	}
	
	public void downloadFile(){	
    	try {
    		// get the filename
            int lastSlash = dwnload_file_path.toString().lastIndexOf('/');
            if (lastSlash >=0) {
            	modulName = dwnload_file_path.toString().substring(lastSlash + 1);
            	modulName = modulName.replace("%20", " ");
            }
    		
    		URL url = new URL(dwnload_file_path);
    		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

    		urlConnection.setRequestMethod("GET");
    		urlConnection.setDoOutput(true);

    		//connect
    		urlConnection.connect();

    		//set the path where we want to save the file    		
    		File SDCardRoot = Environment.getExternalStorageDirectory(); 
    		//create a new file, to save the downloaded file 
    		File file = new File(SDCardRoot, modulName);
    		FileOutputStream fileOutput = new FileOutputStream(file);

    		//Stream used for reading the data from the internet
    		InputStream inputStream = urlConnection.getInputStream();

    		//this is the total size of the file which we are downloading
    		totalSize = urlConnection.getContentLength();

    		runOnUiThread(new Runnable() {
			    public void run() {
			    	pb.setMax(totalSize);
			    }			    
			});
    		
    		//create a buffer...
    		byte[] buffer = new byte[1024];
    		int bufferLength = 0;

    		while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
    			fileOutput.write(buffer, 0, bufferLength);
    			downloadedSize += bufferLength;
    			// update the progressbar //
    			runOnUiThread(new Runnable() {
    			    public void run() {
    			    	pb.setProgress(downloadedSize);
    			    	float per = ((float)downloadedSize/totalSize) * 100;
    			    	cur_val.setText("Downloaded " + downloadedSize + "KB / " + totalSize + "KB (" + (int)per + "%)" );
    			    }
    			});
    		}
    		//close the output stream when complete //
    		fileOutput.close();
    		runOnUiThread(new Runnable() {
			    public void run() {
			    	dialog.dismiss(); // if you want close it..
			    	Toast.makeText(getBaseContext(), "Download Complete. File save to SDCard!", Toast.LENGTH_LONG).show();
			    }
			});    		
    	
    	} catch (final MalformedURLException e) {
    		showError("Error : MalformedURLException " + e);  		
    		e.printStackTrace();
    	} catch (final IOException e) {
    		showError("Error : IOException " + e);  		
    		e.printStackTrace();
    	} catch (final Exception e) {
    		showError("Error : Please check your internet connection " + e);
    	}
    }
    
    void showError(final String err){
    	runOnUiThread(new Runnable() {
		    public void run() {
		    	Toast.makeText(ModulActivity.this, err, Toast.LENGTH_LONG).show();
		    }
		});
    }
    
    void showProgress(String file_path){
    	dialog = new Dialog(ModulActivity.this);
    	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    	dialog.setContentView(R.layout.myprogressdialog);
    	dialog.setTitle("Download Progress");

    	cur_val = (TextView) dialog.findViewById(R.id.cur_pg_tv);
    	cur_val.setText("Starting download...");
    	dialog.show();
    	
    	pb = (ProgressBar)dialog.findViewById(R.id.progress_bar);
    	pb.setProgress(0);
    	pb.setProgressDrawable(getResources().getDrawable(R.drawable.green_progress));  
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (resultCode == RESULT_OK) {
	    	if (requestCode == SELECT_PICTURE) {
		    	Uri selectedImageUri = data.getData();
		    	selectedImagePath    = getPath(selectedImageUri);
		    	String[] separated = selectedImagePath.split("/");
		    	btnBrowseFile.setText("File : " + separated[separated.length - 1]);
		    	ModulPath = Referensi.url + "/modul/" + separated[separated.length - 1];
		    	ModulPath = ModulPath.replace(" ", "%20");
	    	}
    	}
    }
    	 
	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		@SuppressWarnings("deprecation")
		Cursor cursor    = managedQuery(uri, projection, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		
		return cursor.getString(column_index);
	}
	
	public int uploadFile(String sourceFileUri, String upLoadServerUri) {
        String fileName = sourceFileUri;
        HttpURLConnection conn = null;
        DataOutputStream dos = null;  
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024; 
        File sourceFile = new File(sourceFileUri); 
        
        if (!sourceFile.isFile()) {
        	dialogg.dismiss(); 
             runOnUiThread(new Runnable() {
                 public void run() {
                     
                 }
             });
             return 0;
        } else {
             try { 
                 // open a URL connection to the Servlet
                 FileInputStream fileInputStream = new FileInputStream(sourceFile);
                 URL url = new URL(upLoadServerUri);
                  
                 // Open a HTTP  connection to  the URL
                 conn = (HttpURLConnection) url.openConnection(); 
                 conn.setDoInput(true); // Allow Inputs
                 conn.setDoOutput(true); // Allow Outputs
                 conn.setUseCaches(false); // Don't use a Cached Copy
                 conn.setRequestMethod("POST");
                 conn.setRequestProperty("Connection", "Keep-Alive");
                 conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                 conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                 conn.setRequestProperty("uploaded_file", fileName); 
                  
                 dos = new DataOutputStream(conn.getOutputStream());
                 dos.writeBytes(twoHyphens + boundary + lineEnd); 
                 dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + fileName + "\"" + lineEnd);
                 dos.writeBytes(lineEnd);
        
                 // create a buffer of  maximum size
                 bytesAvailable = fileInputStream.available(); 
                 bufferSize = Math.min(bytesAvailable, maxBufferSize);
                 buffer = new byte[bufferSize];
        
                 // read file and write it into form...
                 bytesRead = fileInputStream.read(buffer, 0, bufferSize);  
                    
                 while (bytesRead > 0) {
                	 dos.write(buffer, 0, bufferSize);
                	 bytesAvailable = fileInputStream.available();
                	 bufferSize = Math.min(bytesAvailable, maxBufferSize);
                	 bytesRead = fileInputStream.read(buffer, 0, bufferSize);   
                 }
        
                 // send multipart form data necesssary after file data...
                 dos.writeBytes(lineEnd);
                 dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
        
                 // Responses from the server (code and message)
                 serverResponseCode = conn.getResponseCode();
                 @SuppressWarnings("unused")
				 String serverResponseMessage = conn.getResponseMessage();
                  
                 if(serverResponseCode == 200){              
                     runOnUiThread(new Runnable() {
                          public void run() {
                        	  try {
                  				_ModulName = URLEncoder.encode(txtModulName.getText().toString(), "utf-8");
                  				_ModulPath = URLEncoder.encode(ModulPath, "utf-8");
                  			} catch (UnsupportedEncodingException e) {
                  				// TODO Auto-generated catch block
                  				e.printStackTrace();
                  			} finally {
                  				new GetCategories().execute();
                  			}
                          }
                      });                
                 }    
                  
                 //close the streams //
                 fileInputStream.close();
                 dos.flush();
                 dos.close(); 
            } catch (MalformedURLException ex) {
            	dialogg.dismiss();  
                ex.printStackTrace();
                 
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(ModulActivity.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
            	dialogg.dismiss();  
                e.printStackTrace();
                 
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(ModulActivity.this, "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            dialogg.dismiss();
            return serverResponseCode;  
        } // End else block 
	}
	
	private class GetCategories extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ModulActivity.this);
            pDialog.setMessage("Working...");
            pDialog.setCancelable(false);
            pDialog.show();
 
        }
 
        @Override
        protected Void doInBackground(Void... arg0) {
        	url = Referensi.url+"/service.php?ct=ADDNEWMODUL&ModulName=" + _ModulName + "&ModulPath=" + _ModulPath;
        	call(url);
        	
			return null;
        }
 
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
            Toast.makeText(ModulActivity.this, "File Upload Complete.", Toast.LENGTH_SHORT).show();
            txtModulName.setText("");
            btnBrowseFile.setText("Browse File..");
            showListModul();
        }
 
    }
	
	/**
     * 
     * @param url
     * @return
     * Other method needed to connection with server (address data) 
     */
    public String call(String url) {
    	int BUFFER_SIZE = 2000;
        InputStream in = null;
        try {
            in = OpenHttpConnection(url);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return "";
        }
        
        InputStreamReader isr = new InputStreamReader(in);
        int charRead;
          String str = "";
          char[] inputBuffer = new char[BUFFER_SIZE];          
        try {
            while ((charRead = isr.read(inputBuffer))>0) {                    
                //---convert the chars to a String---
                String readString = 
                    String.copyValueOf(inputBuffer, 0, charRead);                    
                str += readString;
                inputBuffer = new char[BUFFER_SIZE];
            }
            in.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }    
        return str;        
    }
    
    /**
     * 
     * @param urlString
     * @return
     * @throws IOException
     * Other method needed to OpenHttpConnection  
     */
    private InputStream OpenHttpConnection(String urlString) 
    throws IOException {
        InputStream in = null;
        int response = -1;
               
        URL url = new URL(urlString); 
        URLConnection conn = url.openConnection();
                 
        if (!(conn instanceof HttpURLConnection))                     
            throw new IOException("Not an HTTP connection");
        
        try{
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect(); 

            response = httpConn.getResponseCode();                 
            if (response == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();                                 
            }                     
        } catch (Exception ex) {
            throw new IOException("Error connecting");            
        }
        return in;     
    }
}