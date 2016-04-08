package org.king.http;

import java.util.List;

import org.king.BaseActivity;
import org.king.bean.Contact;
import org.king.dao.ContactDao;
import org.king.view.SlideBar;
import org.king.view.adapter.HolderAdapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class SlideActivity extends BaseActivity{
	
	private SlideBar slideBar;
	
	private ListView listView;
	
	private List<Contact> listData;
	
	private ContactAdapter contactAdapter;
	
	private void initUI(){
		slideBar = findView(slideBar, R.id.slideBar); 
		listView = findView(listView, R.id.listView);
		listData = ContactDao.getAllContact(this);
//		System.out.println(ContactDao.getContactByNumber(this, "15171633306").toString());
		contactAdapter = new ContactAdapter(context, listData);
		
		listView.setAdapter(contactAdapter);
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_main);
		 
		initUI(); 
		
	}
	
	
	
	
	
	
	public static class ContactAdapter extends HolderAdapter<Contact,ContactAdapter.ViewHolder>{

		public ContactAdapter(Context context, List<Contact> listData) {
			super(context, listData);
		}

		@Override
		public View buildConvertView(LayoutInflater layoutInflater) {
			// TODO Auto-generated method stub
			return layoutInflater.inflate(R.layout.list_constants_item, null);
		}

		@Override
		public ViewHolder buildHolder(View convertView) {
			ViewHolder holder = new ViewHolder();
			holder.tv_name = (TextView)convertView.findViewById(R.id.tv_name);
			holder.tv_number = (TextView)convertView.findViewById(R.id.tv_number);
			return holder;
		}

		@Override
		public void bindViewDatas(ViewHolder holder, Contact t, int position) {
			ViewHolder h = (ViewHolder)holder;
			if(t!=null){
				h.tv_name.setText(t.getName());
				h.tv_number.setText(t.getPhone());
			}
		}
		
	    public static class ViewHolder {
			
			public TextView tv_name;
			public TextView tv_number;
			
		}
		
	}
	

}
