package proheart.me.phonehelper.domain;

import android.graphics.drawable.Drawable;

public class ProcessInfo {
	private Drawable icon;//进程图标
	private String name;//进程名
	private long memsize;//占用内存
	private boolean usertask;//是否是用户进程
	private String packname;//包名
	private boolean checked;//是否被选中
	
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	@Override
	public String toString() {
		return "TaskInfo [icon=" + icon + ", name=" + name + ", memsize="
				+ memsize + ", usertask=" + usertask + ", packname=" + packname
				+ "]";
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
	public long getMemsize() {
		return memsize;
	}
	public void setMemsize(long memsize) {
		this.memsize = memsize;
	}
	public boolean isUsertask() {
		return usertask;
	}
	public void setUsertask(boolean usertask) {
		this.usertask = usertask;
	}
	public String getPackname() {
		return packname;
	}
	public void setPackname(String packname) {
		this.packname = packname;
	}
	
	
}
