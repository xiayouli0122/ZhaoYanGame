package com.zhaoyan.game.spy;

import android.os.Parcel;
import android.os.Parcelable;

public class SpyWord implements Parcelable {
	
	private String word1;
	private String word2;
	private int group;
	
	private SpyWord(Parcel in){
		readFromParcel(in);
	}
	
	public SpyWord(){
		
	}
	
	public String getWord1() {
		return word1;
	}
	public void setWord1(String word1) {
		this.word1 = word1;
	}
	public String getWord2() {
		return word2;
	}
	public void setWord2(String word2) {
		this.word2 = word2;
	}
	public int getGroup() {
		return group;
	}
	public void setGroup(int group) {
		this.group = group;
	}
	
	public static final Parcelable.Creator<SpyWord> CREATOR  = new Parcelable.Creator<SpyWord>() {

		@Override
		public SpyWord createFromParcel(Parcel source) {
			return new SpyWord(source);
		}

		@Override
		public SpyWord[] newArray(int size) {
			return new SpyWord[size];
		}
	};
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(word1);
		dest.writeString(word1);
	}
	
	public void readFromParcel(Parcel src){
		word1 = src.readString();
		word2 = src.readString();
	}
	
}
