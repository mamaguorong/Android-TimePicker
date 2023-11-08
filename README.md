### 基于github [Android-PickerView](https://github.com/Bigkoo/Android-PickerView)  开发
**在此基础上增加了时间区域选择**

## 介绍

### 1.可以选择时间段


![img.png](https://github.com/mamaguorong/Android-TimePicker/tree/main/preview/img.png)



#### 2.在项目中添加如下代码：

```java  
//时间选择器  
timeRangePickerView = new TimePickerBuilder(this, new OnTimeRangeSelectListener() {  
    @Override  
  public void onTimeRangeSelect(Date dateStart, Date dateEnd, View v) {  
        Toast.makeText(MainActivity.this, getTime(dateStart)+","+getTime(dateEnd), Toast.LENGTH_SHORT).show();  
        Log.i("pvTime", "onTimeSelect");  
    }  
})  
        .setTimeSelectChangeListener(new OnTimeSelectChangeListener() {  
            @Override  
  public void onTimeSelectChanged(Date date) {  
                Log.i("pvTime", "onTimeSelectChanged");  
            }  
        })  
        .setType(new boolean[]{true, true, true, false, false, false})  
        .setLabel("年", "月", "日", "", "", "")  
        .buildRange(); 
```  

