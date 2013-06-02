package com.wiworld.android.man;

 
import java.util.ArrayList;
import java.util.List;

 
import android.app.ActivityManager;
import android.app.ListActivity;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
 
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
 
 

public class MainActivity extends ListActivity implements OnItemClickListener,OnItemLongClickListener{  
	List<Programe> list;
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);  
         
       updateList();
       getListView().setOnItemClickListener(this);
       getListView().setOnItemLongClickListener(this);
       getListView().setCacheColorHint(0x00000000);
    } 
    
    public void updateList(){
        list = getRunningProcess();  
        ListAdapter adapter = new ListAdapter(list,this);  
        getListView().setAdapter(adapter); 
    }
      
    //正在运行的  
    public List<Programe> getRunningProcess(){  
        PackagesInfo pi = new PackagesInfo(this);  
          
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);  
        //获取正在运行的应用  
        List<RunningAppProcessInfo> run = am.getRunningAppProcesses();  
        //获取包管理器，在这里主要通过包名获取程序的图标和程序名  
        PackageManager pm =this.getPackageManager();  
        List<Programe> list = new ArrayList<Programe>();      
          
        for(RunningAppProcessInfo ra : run){  
            //这里主要是过滤系统的应用和电话应用，当然你也可以把它注释掉。  
            if(ra.processName.equals("system") || ra.processName.equals("com.android.phone")||ra.processName.equals("com.wiworld.android.man")){  
                continue;  
            }  
            if(pm==null || pi==null || ra.processName==null || pi.getInfo(ra.processName)==null)
                continue;
            Programe  pr = new Programe(); 
            if(ra.processName!=null)
            	pr.setProcessName(ra.processName);
            if(pm!=null && pi!=null && ra.processName!=null && pi.getInfo(ra.processName)!=null)
            	pr.setIcon(pi.getInfo(ra.processName).loadIcon(pm));  
            if(pi!=null && ra.processName!=null && pi.getInfo(ra.processName)!=null){
            	pr.setName(pi.getInfo(ra.processName).loadLabel(pm).toString());  
            	System.out.println(pi.getInfo(ra.processName).loadLabel(pm).toString());  
            }
            list.add(pr);  
        }  
        return list;  
    }

 

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
	       Log.v("select", ""+position+" : "+list.get(position).getName()+" : "+list.get(position).getProcessName());
	       ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);  
//	       am.restartPackage(list.get(position).getProcessName());
	       am.killBackgroundProcesses(""+list.get(position).getProcessName());
	       
	       
//	       Intent intent = new Intent();
//	       intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
//	       intent.setData(Uri.parse("package:" + list.get(position).getProcessName()));
//	       startActivity(intent);
	       
	       updateList();
	       getListView().invalidate();
	}

	/* 
	 * @see android.widget.AdapterView.OnItemLongClickListener#onItemLongClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> parnet, View view, int position,long id) {
		// TODO Auto-generated method stub
	       Intent intent = new Intent();
	       intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
	       intent.setData(Uri.parse("package:" + list.get(position).getProcessName()));
	       startActivityForResult(intent,1);
		   return false;
	}
	
	/* 
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
	 
	       updateList();
	       getListView().invalidate();;
	}
 
      
} 

   

class ListAdapter extends BaseAdapter {  
    List<Programe> list = new ArrayList<Programe>();  
    LayoutInflater la;  
    Context context;  
      
    public ListAdapter(List<Programe> list ,Context context){  
        this.list = list;  
        this.context = context;  
    }  
      
    @Override  
    public int getCount() {  
        // TODO Auto-generated method stub  
        return list.size();  
    }  
    @Override  
    public Object getItem(int position) {  
        // TODO Auto-generated method stub  
        return list.get(position);  
    }  
    @Override  
    public long getItemId(int position) {  
        // TODO Auto-generated method stub  
        return position;  
    }  
    @Override  
    public View getView(int position, View convertView, ViewGroup parent) {  
        ViewHolder holder;  
        if(convertView == null)  
        {    
            la = LayoutInflater.from(context);  
            convertView=la.inflate(R.layout.list_item, null);  
              
            holder = new ViewHolder();  
            holder.imgage=(ImageView) convertView.findViewById(R.id.image);  
            holder.text = (TextView) convertView.findViewById(R.id.text);  
              
            convertView.setTag(holder);  
        }else{  
            holder = (ViewHolder) convertView.getTag();  
        }  
         final Programe pr = (Programe)list.get(position);  
        //设置图标  
        holder.imgage.setImageDrawable(pr.getIcon());  
        //设置程序名  
        holder.text.setText(pr.getName());  
          
        return convertView;  
    }  
} 

class ViewHolder{  
     TextView text;  
     ImageView imgage;  
} 


class PackagesInfo {  
    private List<ApplicationInfo> appList;  
      
    public PackagesInfo(Context context){  
        //通包管理器，检索所有的应用程序（甚至卸载的）与数据目录  
        PackageManager pm = context.getApplicationContext().getPackageManager();  
        appList = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);  
    }  
      
      
      
    /** 
     * 通过一个程序名返回该程序的一个Application对象。 
     * @param name  程序名 
     * @return  ApplicationInfo  
     */  
      
    public ApplicationInfo getInfo(String name){  
        if(name == null){  
            return null;  
        }  
        for(ApplicationInfo appinfo : appList){  
            if(name.equals(appinfo.processName)){  
                return appinfo;  
            }  
        }  
        return null;  
    }  
      
}  

class Programe {  
    //图标  
    private Drawable icon;    
    //程序名  
    private String name;  
    //package name
    private String processName;
      
    public Drawable getIcon() {  
        return icon;  
    }  
    public void setIcon(Drawable icon) {  
        this.icon = icon;  
    }  
    public String getName() {  
        return name;  
    }  
    public void setName(String name) {  
        this.name = name;  
    }
	public String getProcessName() {
		return processName;
	}
	public void setProcessName(String processName) {
		this.processName = processName;
	}  
    
} 


/*  list_item.xml
<?xml version="1.0" encoding="UTF-8"?>  
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"  
    android:orientation="horizontal"  
    android:layout_width="wrap_content"  
    android:layout_height="wrap_content"  
   >  
    <ImageView  
        android:id="@+id/image"  
        android:layout_width="wrap_content"  
        android:layout_height="wrap_content"  
        android:layout_marginRight="10dip"  
    />   
  <TextView  
    android:id="@+id/text"  
    android:layout_width="wrap_content"  
    android:layout_height="wrap_content"  
  />  
     
</LinearLayout>  
*/