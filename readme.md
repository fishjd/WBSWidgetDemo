

# WBS Widget Demo

[![Release](https://jitpack.io/v/fishjd/WBSWidgetDemo.svg)]
(https://jitpack.io/#fishjd/WBSWidgetDemo)

WBS Widget Demo demonstrates two Android widgets.

- WBSSeekBar
- WBSNotchView

## Installation into your Android Application.  

WBS widget use jitpack.io.  

Add the two sections to your app Build.gradle.  

```
allprojects {
   repositories {
      jcenter()
      maven { url "https://jitpack.io" }
   }
}
```
Add one or both to the dependencies {} section.   

Note these two lines use the less common module format:

compile  'com.gethub.User.Repo:Module:tag'

Most examples use the simpler format without the module:

 compile 'com.gethub.User:Repo:Tag' 

### To add WBSSeekBar: 

```
compile 'com.github.fishjd.WBSWidgetDemo:wbsseekbar:0.1.1'
```

If you prefer the long format: 

```
compile group: 'com.github.fishjd.WBSWidgetDemo', name: 'wbsseekbar' , version: '0.1.1'
```

### To add WBSNotch View: 

```
compile 'com.github.fishjd.WBSWidgetDemo:wbsnotchview:0.1.1'
```

If you prefer the long format: 

```
compile group: 'com.github.fishjd.WBSWidgetDemo', name: 'wbsnotchview' , version: '0.1.1'
```



For more help see:  https://jitpack.io/ or https://jitpack.io/#fishjd/WBSWidgetDemo

To find the latest version go to https://jitpack.io/#fishjd/WBSWidgetDemo

## WBSSeekBar

![WBS_SeekBar](../master/images/WBS_SeekBar.gif)

WBSSeekBar is similar to the [Android SeekBar](https://developer.android.com/reference/android/widget/SeekBar.html), with the addition of a suggest range that may be highlighted.    Several WBSSeekbars may be joined by a vertical bar and move in unison,  this is implemented with a supplied BigVerticalBar class which handles all the details.

### XML Attributes

WBSeekBar is fully configurable in the xml layout file:



| **Range Attributes**                     |                                          |
| ---------------------------------------- | ---------------------------------------- |
| range_min, range_max                     | The range of the SeekBar from right to left. |
| suggested_range_min, suggested_range_max | The range of the 'suggested' values will be highlighted. |
| **Horizontal Bar Attributes**            |                                          |
| draw_bar                                 | Boolean,  True will draw a horizontal bar |
| bar_color                                | The background color                     |
| bar_highlight_color                      | The highlight color bound by suggested_range_min and suggested range_max |
| bar_height                               | the height or thickness of the bar       |
| corner_radius                            | the radius of the circles at the left and right end of the bar. |
| **Thumb Icon Attributes**                | The attributes of the thumb icon         |
| thumb_color                              | The color to draw the thumb icon         |
| thumb_image                              | The image to draw for the thumb.         |
| **Thumb Text Attributes**                | The attributes of the text drawn on the thumb icon. |
| draw_thumb_text                          | Boolean, True will draw the value of the seekbar on the thumb icon. |
| thumb_text_size                          | The size of the thumb text.              |
| thumb_text_color                         | The color of the thumb text.             |
| thumb_text_font                          | The font of the thumb text.              |
| **Vertical Bar Attributes**              | The attributes for the vertical bar that is drawn between seekbars. |
| draw_vertical_bar                        | Enum.  0 = do not draw vertical bar.   1 = draw top half,  2 = draw bottom half, 3 = draw both top and bottom half. |
| vertical_bar_width                       | The width of the vertical bar.           |

### Interface for Callbacks

**TextChangeListener**   - A callback that allows clients to convert the current value to some other text, this text will be displayed as the thumb text.  Allows client to shift the range, add prefix or suffix,  etc.  Use when you want something besides the current value on the thumb text.  If not set then the text is the current value.    Only one allowed pre seek bar.

**ValueChangeListener** - A callback that notifies clients when the progress level has been changed.   Provides the current value and the value returned by TextChangeListener.   Multiple listeners allowed.


## WBSNotchView
![WBS_notchview](../master/images/WBS_NotchView.gif)

WBSNotchView contains the thumb in the center and vertical bars glide left and right.

### XML Attributes

WBSNotchView is fully configurable in the xml layout file:

| **Range Attributes**            |                                          |
| ------------------------------- | ---------------------------------------- |
| range_min, range_max            | The range of the NotchView from right to left. |
| **Thumb Icon Attributes**       | The attributes of the thumb icon         |
| thumb_color                     | The color to draw the thumb icon         |
| thumb_image                     | The image to draw for the thumb.         |
| **Thumb Text Attributes**       | The attributes of the text drawn on the thumb icon. |
| draw_thumb_text                 | Boolean, True will draw the value of the notch view on the thumb icon. |
| thumb_text_size                 | The size of the thumb text.              |
| thumb_text_color                | The color of the thumb text.             |
| thumb_text_font                 | The font of the thumb text.              |
| **Notch Attributes**            |                                          |
| major_count                     | The number of major / big notch marks.  Must be two or larger.   Not recommended, use the default. |
| minor_per_major_count           | The number of minor / small notch marks between every major notch mark. |
| notch_bar_width                 | The distance between min_range and max_range on the notches.  Default is screen_width * 2. |
| notch_bar_width_screen_multiple | Same as notch_bar_width but this number is multiplied by the screen width. |
| major_notch_color               | Color of the major notches               |
| minor_notch_color               | Color of the minor notches               |
| notch_text_size                 | Text size of the text above the notches. |
| notch_text_color                | Text color of the text above the notches. |
| notch_text_font                 | Font of the text above the notches.      |

### Interface for Callbacks

**TextChangeListener**   - A callback that allows clients to convert the current value to some other text, this text will be displayed as the thumb text.  Allows client to shift the range, add prefix or suffix,  etc.  Use when you want something besides the current value on the thumb text.  If not set then the text is the current value.    Only one allowed pre notch view .

**ValueChangeListener** - A callback that notifies clients when the progress level has been changed.   Provides the current value and the value returned by TextChangeListener.  
​



