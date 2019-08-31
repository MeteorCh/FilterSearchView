# FilterSearchView
## 简介
### 在Toolbar下方弹出的，带筛选条件的并且能记录搜索历史并自动补全的搜索对话框    
&#8194;&#8194;&#8194;&#8194;项目终于要做完了，这篇博客应该是最近写的有关安卓的最后一篇博客了，不知道毕业以后还会不会做安卓(￣.￣)，最近很用心的将手上的这个项目做完了，然后这个搜索筛选模块不管是界面还是交互逻辑感觉自己写的还不错，放在这里供大家参考。
&#8194;&#8194;&#8194;&#8194;在进行列表展示的时候，经常需要对列表中的数据进行查询，筛选展示，此时如果进入一个新的页面，用户体验会不太好，而且搜索的各种回调也不是很好处理。个人感觉比较好的方法是弹出一个全屏的，能和当前界面风格比较搭配的界面，那这种情况下，全屏的对话框可能是一个比较好的选择。最近在做项目的过程中，自己设计了一套搜索筛选的逻辑界面逻辑，感觉用户体验还不错，具体效果见下图：  
![演示图片](https://img-blog.csdnimg.cn/2019083014381140.gif)
## 主要功能
  * 直接在数据展示界面弹出一个全屏的带搜索框的对话框，通过Litepal数据库保存搜索历史。最大历史条数设置为10条，当输入的记录超过10条时，自动删除最久的历史记录。历史记录高度随内容自动调整，点击遮罩区搜索界面消失。
  * 在搜索界面，按下筛选按钮，在Toolbar下方弹出一个筛选界面。通过按照规定布局（具体见使用方法）进行布局，可以自动调整筛选界面内容界面和遮罩界面的位置。
## 使用方法：
### 1.继承BaseBelowToolbarDialog这个类写自己的筛选条件类，这个类是弹出的筛选对话框，也就是上图中弹出的“筛选条件的”对话框。继承该类的构造函数需要三个参数  
   * 参数1：Contex
   * 参数2：LayoutID：筛选条件的布局文件。为了能有点击外部消失的效果，需要注意，这个布局文件中，有两个部分：第一个是content部分，第二个是遮罩部分。如下图所示：  
![image](https://img-blog.csdnimg.cn/20190830143848552.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzMxNzA5MjQ5,size_16,color_FFFFFF,t_70)    
 &#8194;&#8194;&#8194;&#8194;其中，content部分用FragmentLayout或者LinearLayout包裹一层，并设置ID为content_layout。遮罩部分最好就按照我demo_filter_layout中的那样，用一个半透明的View来代替，其ID为mask_layout。上面的两个ID名是固定的，不要更改。content_layout是来计算内容高度然后设置对话框位于Toolbar下方的。mask_layout是用来筛选对话框弹出时，空余部分有遮罩效果并且点击遮罩筛选对话框消失的。具体就参考我的demo_filter_layout来设计即可。
   
   * 参数3：toolBarHeight：toolbar的高度，一般为54dp，记得转换为像素。  
   该类的子类中，如果要监听筛选条件的确定键按下的事件，则在创建下面的弹出SearcherDialog时，传入一个监听回调。此监听回调是通过HashMap来保存筛选条件的，具体见DemoFilterDialog这个类的实现。
### 2.在需要弹出查询对话框的地方，弹出SearcherDialog。此类已经写好了，可以直接使用，构造函数需要三个参数：  
   * 参数1：Contex  
   * 参数2：开始搜索的回调：当用户在键盘上按下搜索按钮时，会调用此回调。回调的参数是搜索框中输入的关键字。  
   * 参数3：筛选对话框中，确定按下的回调：当用户在筛选界面上按下确定时，会调用此回调，回调的参数是一个HashMap，里面存放了各种筛选条件。  
   * 参数4：当前的类名：由于一个程序中可能会有多个搜索对话框，每个对话框应该单独记录搜索历史记录。故传入当前类，用来识别是哪个界面的搜索记录。  
## 注意
&#8194;&#8194;&#8194;&#8194;因为筛选的历史条件是通过Litepal来保存的，所以使用的时候，记得引用Litepal，在Application中初始化，在assets的litepal.xml中对类进行声明。具体见我的程序，这里不再赘述。  
## 修改记录
* **8.31号修改记录**
&#8194;&#8194;&#8194;&#8194;实现沉浸式式界面，即弹出搜索界面时，状态栏颜色和搜索框的背景一致，且状态栏文字的颜色变为黑色（以前状态栏的颜色是半透明的黑色，不是很好看）。如下图所示：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190831213035533.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzMxNzA5MjQ5,size_16,color_FFFFFF,t_70)
   
   
