//package org.king.http;
//
//import java.util.List;
//import java.util.Random;
//
//import org.king.BaseActivity;
//import org.king.bean.User;
//import org.litepal.crud.DataSupport;
//
//import android.content.ContentValues;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.TextView;
//
//public class TestLitePalActivity extends BaseActivity{
//	
//	private EditText et,et_update;
//	
//	private TextView tv;
//	
//	
//	private void initUI(){
//		et = findView(et, R.id.et);
//		et_update = findView(et_update, R.id.et_update);
//		tv = findView(tv, R.id.tv);
//		
//		
//	}
//	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_test);
//		initUI();
//	}
//	
//	public int random(int min,int max){
//		return new Random().nextInt(max-min)+min;
//	}
//	public String randomSex(){
//		
//		int num = random(1, 10);
//		if(num>5){
//			return "男";
//		}
//		return "女";
//		
//	}
//	
//	private void insert(){
//		User user = new User();
//		user.setsId(9);
//		user.setName("用户" + random(0, 30));
////		user.setAge(random(10, 50));
//		user.setSign("签名" + random(0, 30));
//		user.setSex(randomSex());
//		if(user.save()){
//			showToast("success");
//		}
//		
//		
//	}
//	private void delete(){
//		DataSupport.deleteAll(User.class, "name = ?",et_update.getText().toString());
//	}
//	private void update(){
//		ContentValues values = new ContentValues();
//		values.put("sign", "修改签名" + random(0, 30));
//		values.put("sId", random(0, 30));
//		values.put("name", et_update.getText().toString()+ random(0, 30));
//		DataSupport.updateAll(User.class,values, "id = ?",et.getText().toString());
//	}
//	private void query(){
//		List<User> list = DataSupport.findAll(User.class);
//		System.out.println(list);
//		tv.setText(list.toString());
//	}
//	
//	public void onClick(View v){
//		switch (v.getId()) {
//		case R.id.btn_insert:
//			insert();
//			break;
//		case R.id.btn_delete:
//			delete();
//			break;
//		case R.id.btn_update:
//			update();
//			break;
//		case R.id.btn_query:
//			query();
//			break;
//
//		default:
//			break;
//		}
//	}
//
//}
