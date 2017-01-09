package proheart.me.phonehelper.domain;

import android.graphics.drawable.Drawable;

public class AppInfo {
	private Drawable icon;//APP图标
	private String name;//app名称
	private String packname;//包名
	private boolean inRom;//是否在内存中
	private boolean userApp;//是否是用户应用
	private int uid;
	private long txByte;//上传流量
	private long rxByte;//下载流量

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public long getTxByte() {
		return txByte;
	}

	public void setTxByte(long txByte) {
		this.txByte = txByte;
	}

	public long getRxByte() {
		return rxByte;
	}

	public void setRxByte(long rxByte) {
		this.rxByte = rxByte;
	}

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
	public String getPackname() {
		return packname;
	}
	public void setPackname(String packname) {
		this.packname = packname;
	}
	public boolean isInRom() {
		return inRom;
	}
	public void setInRom(boolean inRom) {
		this.inRom = inRom;
	}
	public boolean isUserApp() {
		return userApp;
	}
	public void setUserApp(boolean userApp) {
		this.userApp = userApp;
	}

	@Override
	public String toString() {
		return "AppInfo{" +
				"icon=" + icon +
				", name='" + name + '\'' +
				", packname='" + packname + '\'' +
				", inRom=" + inRom +
				", userApp=" + userApp +
				", uid=" + uid +
				", txByte=" + txByte +
				", rxByte=" + rxByte +
				'}';
	}
}
