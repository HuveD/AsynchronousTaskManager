![Title](https://user-images.githubusercontent.com/48434145/71309308-d439ed00-2449-11ea-80ae-540b500dfe3e.png)

![ReleaseVer](https://user-images.githubusercontent.com/48434145/71309272-64c3fd80-2449-11ea-9166-36e998e69a52.png)
![AndroidMinVer](https://user-images.githubusercontent.com/48434145/71309273-668dc100-2449-11ea-8e63-6d65beb92d44.png)

:star2: 'AsynchronousTaskManager' called 'ATM' by omitting is a background task helper.

:atm: ATM is designed for background work that is free from the activity.
Regardless of "Activity," tasks can be shared anywhere.
If you don't want to share, it can do that, also.

:punch: You can use 'Atm' to perform multiple tasks quickly and easily! :punch:

<br/>

## :notebook:Table of Contents
1. [Quick Start](#quick-start)
	1. [Gradle](#gradle-setup)
3. [Simple Usage](#simple-usage)
2. [Example Source](#example-source)
4. [License](#free)

<br/>

<h2 id="quick-start">:speedboat: Quick Start</h2>

Add the library to your Android project, then check out the examples below!

### Gradle Setup

```gradle-setup
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.HuveD:AsynchronousTaskManager:1.1.1'
}
```

<br/>

<h2 id="simple-usage">:+1: Simple Usage</h2>

```
AtmTask atmTask = AsynchronousTaskManager.getInstance().createTask(new Runnable() {
	@Override
    public void run() {
    	// Do here that wants to do in the background thread.
    }
}, 0);
atmTask.active();
```

#### [Create the background task]
**#createTask(Runnable, int)**
**'Runnable'** is about you working in the background thread.
**'int'** is the **unique** id of this task.

<br/>

#### [Start the task]
If you invoke **'#active()'**, this task will start.

<br/>

<h2 id="example-source">:eyes: Example Source</h2>

If you want more details, look at the look at the [source code](https://github.com/JeongHyeonYoo/AsynchronousTaskManager/blob/master/AsynchronousTaskManagerExample/src/main/java/kr/co/huve/TaskManagerExample/ExampleActivity.java).

The source code has an example of usage, like below.
- Perform another action after the task is finished
- To change the Ui in 'Atm'
- Detect a task's status change
- Get the task result
- Error catch


<br/>


<h1 id="free">:page_facing_up: License</h1>

Copyright (c) 2019 JeongHyeonYoo

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

<br/>

