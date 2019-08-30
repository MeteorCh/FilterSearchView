# FilterSearchView
在Toolbar下方弹出的，带筛选条件的并且能记录搜索历史并自动补全的搜索对话框
在进行列表展示的时候，经常需要对列表中的数据进行查询，筛选展示，此时如果进入一个新的页面，用户体验会不太好，最近在做项目的过程中，自己设计了一套搜索筛选的逻辑界面逻辑，感觉用户体验还不错，具体效果见下图：

![Image](https://github.com/MeteorCh/FilterSearchView/blob/master/art/Screenrecorder.gif)

# 使用方法：
1.继承BaseBelowToolbarDialog这个类，这个类是弹出的筛选对话框，也就是上图中弹出的“筛选条件的”对话框
