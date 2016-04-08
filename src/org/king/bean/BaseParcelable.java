package org.king.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class BaseParcelable implements Parcelable{
	

	
	public BaseParcelable(Parcel in){
		super();
		//TODO 读取
//		s = in.readString();
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		//TODO 写入
		
//		out.writeString(...);
	}
	
	public static final Parcelable.Creator<BaseParcelable> CREATOR = new Creator<BaseParcelable>() {

		@Override
		public BaseParcelable createFromParcel(Parcel source) {
			return new BaseParcelable(source);
		}

		@Override
		public BaseParcelable[] newArray(int size) {
			
			return new BaseParcelable[size];
		}
		
	};

}
