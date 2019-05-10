# Netty

​	一款异步的事件驱动的网络应用程序框架

阻塞   发起  ----》阻塞.... 阻塞  -----》完成

非阻塞   检查...检查----》阻塞----》完成

​	阻塞与非阻塞是进程在访问数据的时候，数据是否准备就绪的处理方式，数据没准备好，要么等待数据准备好处理，要么直接返回即不处理

同步    **双方的动作是经过双方协调的，步调一致的**

异步    **双方并不需要协调，都可以随意进行各自的操作**

​	同步与异步都是基于应用程序和操作系统处理IO时间采用的方式，要么应用程序直接参与IO操作，要么IO操作交给操作系统去处理，应用程序只要等待通知

事件驱动：指在持续事务管理过程中，进行决策的一种策略，即跟随当前时间点上出现的事件，调动可用资源，执行相关任务，使不断出现的问题得以解决，防止事务堆积。（就是回调）

bio：阻塞同步io

nio：非阻塞io

aio：非阻塞异步io

​	**为什么Netty使用NIO而不是AIO？**

1. Netty不看重Windows上的使用，在Linux系统上，AIO的底层实现仍使用EPOLL，没有很好实现AIO，因此在性能上没有明显的优势，而且被JDK封装了一层不容易深度优化

2. Netty整体架构是reactor模型, 而AIO是proactor模型, 混合在一起会非常混乱,把AIO也改造成reactor模型看起来是把epoll绕个弯又绕回来

3. AIO还有个缺点是接收数据需要预先分配缓存, 而不是NIO那种需要接收时才需要分配缓存, 所以对连接数量非常大但流量小的情况, 内存浪费很多

4. Linux上AIO不够成熟，处理回调结果速度跟不到处理需求，比如外卖员太少，顾客太多，供不应求，造成处理速度有瓶颈（待验证）

   BIO是面向流的，一位置每次从流中读取字节，直至读取完全部字节，他们没有缓存在任何地方，因此是不能前后移动流中数据，需要移动或者操作的话需要将其缓存到缓冲区。

   NIO是面向缓冲区的，数据读取到一个稍后处理的缓冲区，当然可以前后移动或者操作缓冲区数据。

## 1.组件

### 	1.Channel

​		Java NIO的基本构造，代表一个到实体的开放连接，如读操作和写操作

### 	2.回调

​		异步处理的后续操作

### 	3.Future

​		提供了另一种操作完成时通知应用程序的方式，可以看作是异步操作结果的占位符，它在未来的某个时刻完成，并提供对其结果的访问，相对于jdk netty提供了自己的实现ChannelFuture 用的是ChannelFutureListener，即监听器

### 	4.事件和ChannelHandler

​	事件就是 网络事件的出入站等，而ChannelHandler 则是对应具体事件的处理

### 	5.放在一起

​	 Netty的异步编程模型建立在Future和回调上，并将事件派发到ChannelHandlerf方法。

​	触发事件 通过 抽象的Selector 进行派发代码，在内部为每个channel 分配EventLoop （线程驱动），来处理所有的事件 

# 2.第一个应用

# 3.netty的组件和设计

## 	1.Channel EventLoop  ChannelFuture  网络抽象

​		Channel   一个连接 socket  （提供了很多默认实现）

​		EventLoop 控制流、多线程、并发  （相等于线程，EventLoop 相当于线程池）一个Channel  绑定一个 		

​	EventLoop ，但是一个EventLoop  可能会分配给一个或多个Channel   

​		ChannelFuture   异步通知

## 	2.ChannelHandler ChannelPipeline  管理数据流以及执行应用程序处理逻辑

​		ChannelHandler  

​			①入站和出站的数据的程序逻辑容器，②编解码，③异常通知，④channel编程活动或非活动的通知，⑤

​			注册Eventloop，或者注销  Eventloop的通知，⑥用户自定义的事件通知

​			常用的   ChannelHandlerAdapter 

​					ChannelInboundHandlerAdapter

​					ChannelOutboundHandlerAdapter

​					ChannelDuplexHandler

​					编码器、解码器

​					SimpleChannelInboundHandler<T> T 需要处理的数据类型  ctx是可以继续传递下去

​		ChannelPipeline   （拦截过滤器实现）

​			是ChannelHandler  处理链的容器 出站和入站方向相反

​		引导 

​			为应用程序网络层配置提供容器，将进程绑定和端口或者将进程连接到某个指定主机的指定端口进程，面

​		向连接的协议，如 tcp

​			Bootstrap   一个EventLoopGroup

​			ServerBootstrap  两个EventLoopGroup （当然也可以共用一个） 一个用来监听服务，一个用来处理传入

​			客户端的连接

# 4.传输

​	OIO（阻塞）、NIO（非阻塞）、Local（JVM内部的异步通信）、Embedded（测试channelHandler）

​	netty的api 比较统一，只需要少量修改

## 	1.传输的api

​	channel  线程安全的

​		EventLoop eventLoop(); 返回分配的eventLoop

​		ChannelPipeline pipeline(); 返回分配的ChannelPipeline 

​		boolean isActive(); 是否是活动的

​		SocketAddress localAddress(); 返回本地的 SocketAddress 

​		SocketAddress remoteAddress(); 返回远程的 SocketAddress 

​		ChannelFuture write(Object var1); 将数据写到远程节点，这个数据传递给 ChannelPipeline 写队列的第一个

​		Channel flush(); 将之前写的数据 清空缓冲区数据 冲刷到 底层传输

​		ChannelFuture writeAndFlush(Object var1); 写然后冲刷

## 	2.内置的传输

​		提供的开箱可用的传输

​		1.NIO  io.netty.channel.socket.nio     java NIO为基础

​			选择器背后就是个注册表，当channel 发生变化时，得到通知，可能的变化：

​				新的channel已经接受并且就绪 OP_ACCEPT

​				channel连接已经完成 OP_CONNECT

​				channel有已经就绪的可供读取的数据  OP_READ

​				channel可用于写数据	OP_WRITE

​				zero-copy（直接将数据从文件移动到网络接口）

​		2.Epoll io.netty.channel.epoll      基于JNI驱动的epoll()和非阻塞IO，在linux上更快，比NIO更快

​			linux jdk nio 使用了这一特性，但是netty 做了自己的统一封装 （使用了更加轻量的中断）比jdk 更高效

​			替换的话 Epoll 的serverSocket和EvevtLoopGroup	

​		3.OIO io.netty.channme.socket.oio 使用java.net 包的阻塞流

​			适用于某些阻塞的调用库（jdbc）等

​		4.Local io.netty.channel.local  在JVM内部通过管道进行通信的本地传输​			

​		5.Embedded io.netty.channel.embedded  Embedded传输，允许使用channelhandler不是真正的网络传输，为

​		  了测试channelHandler

| 传输             | TCP  | UDP  | SCT  | UDT  |
| ---------------- | ---- | ---- | ---- | ---- |
| NIO              | √    | √    | √    | √    |
| Epoll（仅linux） | √    | √    | ×    | ×    |
| OIO              | √    | √    | √    | √    |



# 5.ByteBuf

​	netty的数据容器（jdk  nio 数据容器是 ByteBuffer）

## 	1.ByteBuf 

​		维护两个索引（一个写，一个读）当两个索引重叠，就到达了可以读的末尾，可以指定容量

### 		1.使用模式	

​		堆缓冲区 （JVM 的堆空间）别名 支撑数组，在没有使用池化的情况下提供快速的分配和释放，非常适合有遗留

​		数据的处理。类似于jdk的ByteBuffer使用

```java
ByteBuf b=...;
//支撑数组
if(b.hasArray()){
    byte[] array= b.array();
    //可读的头
    int offset=array.arrayOffset()+array.readerIndex();
    //可读的尾
    int length=array.readableBytes();
    //可读数据处理
    handleArray(array,offset,length);
}
```

​		直接缓冲区 （操作的直接缓冲区，垃圾回收器之外）

```java
ByteBuf b=...;
//直接缓冲区
if(!b.hasArray()){
    int length= b.readableBytes();
    byte[] array= new byte[length];
   	b.getBytes(b.readerIndex(),array);
    //可读数据处理
    handleArray(array,0,length);
}
```

​		复合缓冲区 （为多个ByteBuf 提供一个聚合视图） CompositeByteBuf 实现

```java
ByteBuf head=...;
ByteBuf body=...;
CompositeByteBuf compositeByteBuf =Unpooled.compositeByteBuf();
//添加
compositeByteBuf.addComponents(head,body);
//移除
//compositeByteBuf.remove(0);
for(ByteBuf byteBuf:CompositeByteBuf ){
    //单个访问
}
//其自身的访问类似于缓冲区访问 同上
```

### 		2.字节级的操作

```java
int capacity(); //返回容量

int maxCapacity();  //返回最大允许的容量

byte getByte(int var1); //根据下表访问数据 不会改变指针

ByteBuf discardReadBytes();  //丢弃已读字段，并回收空间，进行内存复制，将可读内容复制到ByteBuf 的头

//任何 read/skip 开头的操作都将检索或者跳过操作，移动读指针

ByteBuf readBytes(ByteBuf dst); //写的指针也会变更

//任何 write开头的操作都将写操作，移动写指针

boolean isReadable(); // this.writerIndex - this.readerIndex >0

boolean isWritable(); //this.capacity - this.writerIndex >0

int writableBytes(); //this.capacity - this.writerIndex

ByteBuf writerIndex(int var1); //移动写指针到指定位置

ByteBuf readerIndex(int var1); //移动读指针到指定位置 

ByteBuf clear();  //readerIndex  writerIndex 都改为0，并不会清除内容

int indexOf(int fromIndex, int toIndex, byte value); // 在坐标范围内查找

int forEachByte(ByteProcessor processor); //通过 ByteProcessor  查找 提供了很多查找

//派生缓冲区 返回不安全的副本

ByteBuf duplicate();// 复制

ByteBuf slice();  //分片

ByteBuf slice(int index, int length);// 分片

ByteBuf order(ByteOrder endianness); //（已经废弃）字节序排序后的

ByteBuf readSlice(int length); //返回 readerIndex开始 length的副本

//Unpooled 类  ByteBuf unmodifiableBuffer(ByteBuf buffer) ; （已经废弃） 返回一个只读的缓冲

//复制 返回独立的副本

ByteBuf copy(); 

ByteBuf copy(int index, int length);

//get/set 开头的方法 从给定的索引开始不会改变索引

//read/write 开头的操作 从给定的索引开始，并根据改变的字节数改变索引
```

​		

## 	2.ByteBufHolder 进行属性扩展 ，池化缓冲区等

​		

```java
ByteBuf content(); //返回这个所持有的 ByteBuf 

ByteBufHolder copy(); // 深拷贝

ByteBufHolder duplicate(); //深拷贝属性+共享的 ByteBuf 
```



## 3.ByteBuf 分配

### 	1.按需分配 ByteBufAllocator （ByteBuf 的池化）（通过channel.alloc() 或者ChannelHandler 的ctx.alloc();)

```java
ByteBuf buffer(int initialCapacity, int maxCapacity);

ByteBuf buffer(int initialCapacity);

ByteBuf buffer();

//以上返回一个基于堆或者基于直接内存存储的ByteBuf

ByteBuf heapBuffer();

ByteBuf heapBuffer(int initialCapacity);

ByteBuf heapBuffer(int initialCapacity, int maxCapacity);

//以上返回一个基于堆存储的ByteBuf

ByteBuf directBuffer();

ByteBuf directBuffer(int initialCapacity);

ByteBuf directBuffer(int initialCapacity, int maxCapacity);

//以上返回一个基于直接内存存储的ByteBuf

CompositeByteBuf compositeBuffer();

CompositeByteBuf compositeBuffer(int maxNumComponents);

CompositeByteBuf compositeHeapBuffer();

CompositeByteBuf compositeHeapBuffer(int maxNumComponents);

CompositeByteBuf compositeDirectBuffer();

CompositeByteBuf compositeDirectBuffer(int maxNumComponents);		

//以上返回一个CompositeByteBuf （可以指定添加ByteBuf数量）

//ByteBufAllocator 有两种实现 PooledByteBufAllocator   UnpooledByteBufAllocator

//PooledByteBufAllocator   池化了ByteBuf 并减少内存碎片（jemalloc 内存分配）

//UnpooledByteBufAllocator  每次调用返回一个新的ByteBuf

//默认是PooledByteBufAllocator    可以在channelConfig api 中修改
```

### 	2.Unpooled 缓冲区

​	提供了静态方法创建非池化的ByteBuf 实例

```java
public static ByteBuf buffer() 

public static ByteBuf buffer(int initialCapacity)

public static ByteBuf buffer(int initialCapacity, int maxCapacity)

//以上返回未池化的基于栈内存储的ByteBuf

public static ByteBuf directBuffer() 

public static ByteBuf directBuffer(int initialCapacity)

public static ByteBuf directBuffer(int initialCapacity, int maxCapacity)

//以上返回未池化的基于直接内存存储的ByteBuf

public static ByteBuf wrappedBuffer //返回包装了给定数据的ByteBuf

public static ByteBuf copiedBuffer //返回一个复制了给定数据的ByteBuf
```

### 	3.ByteBufUtil

​	提供用于操作 ByteBuf的静态方法 api通用无关池化与否

```java
public static String hexDump(ByteBuf buffer)  //以16进制形式打印 ByteBuf的内容

public static boolean equals(ByteBuf bufferA, ByteBuf bufferB) //比较两个ByteBuf
```



## 4.引用计数

​	就是查看一个对象的是否是活着的依据

​	为ByteBuf 和ByteBufHolder 实现接口  ReferenceCounted

​	release() 释放相对应的资源

# 6.ChannelHandler 和 ChannelPipeline

## 	1.ChannelHandler 	

 **Channel 的生命周期**
$$
ChannelRegistred(注册)->ChannelActive(活动)->ChannelInactive(断开)->ChannelUnregistred(未注册)
$$


  **ChannelHandler 的生命周期**
$$
handlerAdd(添加)->handlerRemoved(移除)->exceptionCaught(异常处理)
$$
   **ChannelInBoundHandler 接口**

​	加Channel 的生命周期

```java

void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception ;//读数据时 调用

void channelReadComplete(ChannelHandlerContext ctx) throws Exception;// 当读数据操作完成时 调用

void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception; //可写状态发生变化时 调用

//与可写性相关的阈值可以通过channel.config().setWriteHighWaterMark(int writeBufferHighWaterMark)

//channel.config().setWriteLowWaterMark(int writeBufferHighWaterMark)

void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception;//如果触发了用户事件则调它

//SimpleChannelInboundHandler的 channelRead0 会自动释放资源 所以，你不要存储指向任何消息的引用以供未来
//使用，因为会失效
```



   **ChannelOutBoundHandler 接口**

​	一个就强大的功能是可以按需推迟或者延迟操作或事件，这使得可以通过一些复杂的方法来处理请求，例如：远程节

​	点写入被暂停，那么你可以推迟冲刷操作并在稍后继续​	

```java
void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception;  //请求将channel 绑定到本地连接地址时被调用
void connect(
        ChannelHandlerContext ctx, SocketAddress remoteAddress,
        SocketAddress localAddress, ChannelPromise promise) throws Exception;// 当请求将channel连接到远程节点时被调用
void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception; //当请求将channel 从远程节点断开时 被调用
void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception; //当请求关闭channel时 被调用
void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception; //当请求将channel 从他的evevtLoop 注销时被调用
void read(ChannelHandlerContext ctx) throws Exception; //当请求从 channel 读取更多数据时被调用
void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception; //当请求 通过channel将数据写到远程节点时被调用
void flush(ChannelHandlerContext ctx) throws Exception;//当请求通过 channel 将入队数据冲刷到远程节点时被调用

// 参数 ChannelPromise 是 ChannelFuture 的子类，以便在操作完后得到通知  
```

​	**我们自定义处理时，可以以ChannelInboundHandlerAdapter和 ChannelOutboundHandlerAdapter 为起始点重**

​	**写我们感兴趣的方法即可**

​	ChannelInboundHandler的channelread() 以及  ChannelOutboundHandler的channelwrite() 当某个ByteBuf 完全被消

​	费了，那么应该**释放资源**，Netty提供了 ResourceLeakDetector  对应用程序的缓冲区去分配1%的采样检测内存泄漏

​	对于入站 SimpleChannelInboundHandler 的read0() 会自动释放不用的资源 而对于出站那么需要自己调用​	

```java
//ReferenceCountUtil 类的方法
public static boolean release(Object msg) 
// 并通知ChannelPromise  
 promise.setSuccess();//通知消息已被处理   
```



## 2.ChannelPipeline（与channel 永久性的分配 1:1）

```java
*  +---------------------------------------------------+---------------+
*  |                           ChannelPipeline         |               |
*  |                                                  \|/              |
*  |    +---------------------+            +-----------+----------+    |
*  |    | Inbound Handler  N  |            | Outbound Handler  1  |    |
*  |    +----------+----------+            +-----------+----------+    |
*  |              /|\                                  |               |
*  |               |                                  \|/              |
*  |    +----------+----------+            +-----------+----------+    |
*  |    | Inbound Handler N-1 |            | Outbound Handler  2  |    |
*  |    +----------+----------+            +-----------+----------+    |
*  |              /|\                                  .               |
*  |               .                                   .               |
*  | ChannelHandlerContext.fireIN_EVT() ChannelHandlerContext.OUT_EVT()|
*  |        [ method call]                       [method call]         |
*  |               .                                   .               |
*  |               .                                  \|/              |
*  |    +----------+----------+            +-----------+----------+    |
*  |    | Inbound Handler  2  |            | Outbound Handler M-1 |    |
*  |    +----------+----------+            +-----------+----------+    |
*  |              /|\                                  |               |
*  |               |                                  \|/              |
*  |    +----------+----------+            +-----------+----------+    |
*  |    | Inbound Handler  1  |            | Outbound Handler  M  |    |
*  |    +----------+----------+            +-----------+----------+    |
*  |              /|\                                  |               |
*  +---------------+-----------------------------------+---------------+
*                  |                                  \|/
*  +---------------+-----------------------------------+---------------+
*  |               |                                   |               |
*  |       [ Socket.read() ]                    [ Socket.write() ]     |
*  |                                                                   |
*  |  Netty Internal I/O Threads (Transport Implementation)            |
*  +-------------------------------------------------------------------+
```

​	ChannelHandlerContext 会在 ChannelPipeline 的处理链上 传递

```java
ChannelPipeline addFirst();
ChannelPipeline addBefore();
ChannelPipeline addAfter();
ChannelPipeline addLast();
//以上均为 添加到 ChannelPipeline 中
ChannelHandler remove(); //移除
// 替换
<T extends ChannelHandler> T replace();
List<String> names()；// 返回所有名字
ChannelHandlerContext context(ChannelHandler handler); //返回该  ChannelHandler 持有的 context
get(); //获取 一个指定类型或者名字的 ChannelHandler
```

​	那么 ChannelHandler 的事件，其实都是 ChannelPipeline 触发的

```java
//入站
ChannelPipeline fireChannelRegistered(); // 触发下一个 入站ChannelHandler的 channelRegistered
ChannelPipeline fireChannelUnregistered();// 触发下一个 入站ChannelHandler的 channelUnregistered
ChannelPipeline fireChannelActive(); // 触发下一个 入站ChannelHandler的 channelActive
ChannelPipeline fireChannelInactive(); // 触发下一个 入站ChannelHandler的 channelInactive
ChannelPipeline fireExceptionCaught(Throwable cause);// 触发下一个 入站ChannelHandler的 															//exceptionCaught() 
ChannelPipeline fireUserEventTriggered(Object event); //触发下一个 入站ChannelHandler的 														//userEventTriggered
ChannelPipeline fireChannelRead(Object msg);//触发下一个 入站ChannelHandler的ChannelRead
ChannelPipeline fireChannelReadComplete();//触发下一个 入站ChannelHandler的ReadComplete
ChannelPipeline fireChannelWritabilityChanged();//触发下一个 入站ChannelHandler的 				
											//ChannelWritabilityChanged
//出站
ChannelFuture bind(SocketAddress localAddress);// 触发下一个 出站ChannelHandler的 bind
ChannelFuture connect(SocketAddress remoteAddress);// 触发下一个 出站ChannelHandler的 connect
ChannelFuture disconnect();// 触发下一个 出站ChannelHandler的 disconnect
ChannelFuture close();// 触发下一个 出站ChannelHandler的 close
ChannelFuture deregister();// 触发下一个 出站ChannelHandler的 deregister
ChannelOutboundInvoker flush();// 触发下一个 出站ChannelHandler的 flush
ChannelFuture write(Object msg);// 触发下一个 出站ChannelHandler的 write
ChannelFuture writeAndFlush(Object msg);// 触发下一个 出站ChannelHandler的 writeAndFlush
ChannelOutboundInvoker read();// 触发下一个 出站ChannelHandler的 read
```

**ChannelHandlerContext接口 处理事件和channel或者channelPiple处理事件不同在于：第一个处理的结果会沿着 事件处理链传播，而第二个则会沿着 真个channelPiple的进行传播**

**其Api大多与channelPiple 相同我们着重找些不同的看看**

```java
ByteBufAllocator alloc(); //返回和这个实例相关的Channel 配置的ByteBufAllocator
EventExecutor executor(); //返回调度事件的线程池
ChannelHandler handler(); //返回绑定到这个实例的 ChannelHandler
boolean isRemoved(); //返回是否被channelPiple 移除
ChannelPipeline pipeline(); //返回 ChannelPipeline
ChannelHandlerContext read(); //将数据从channel 读取到第一个入站的缓冲区，会触发一个 channelRead 事件，								//可以通过 ChannelConfig.setAutoRead(true) 让其自动调用读取
```

**注意：** ChannelHandlerContext 传播事件比较短，所以尽量用这个，ChannelHandlerContext与channelhandler之间的关系是绑定的，所以缓存它的引用时安全的

## 3.ChannelHandler 和ChannelHandlerContext 高级用法

### 	1.实现动态调整channelPiple 中的 ChannelHandler 

### 	 2.缓存ChannelHandlerContext 在稍后的处理中使用

一个ChannelHandler  可以被多个 ChannelPipeline 所持有（@Sharable ），但是为了保证线程安全，请不要持有任何状

态（成员变量）,或者可以通过 改变该状态的方法 改为 同步的，（共享的ChannelHandler  可以用来收集不同channel

的统计信息）

## 4.异常处理

### 	1.入站

​	从触发异常的地方在整个ChannelPipeline 里面向后传递,netty会将此异常传递，若没处理，那么会被记录未处理

```java
void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception; 
```

### 	2.出站

```java
//channelHandlerContext 的write  可以在promise.setSuccess() setFailure()
void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception;
//以下为 channel 或者ChannelPipleLine 的方法
//出站操作否会返回ChannelFuture注册到ChannelFuture的 ChannelFutureListener会在操作完成时被通知 成功与否
ChannelFuture write(Object msg);
ChannelFuture write(Object msg, ChannelPromise promise);
```

**注册到ChannelFuture的 ChannelFutureListener 更好，更简单**

# 7.EvevtLoop和线程模型

EvevtLoop 绑定一个线程，负责挂在其名下的channel的全部IO 操作

## 1.线程模型概述

线程模型：指定了操作系统、编程语言、框架或者应用程序的上下文中的线程管理的关键方面

按需的Thread->excutor(仅优化了线程创建和销毁的步骤)并不能消除有上下文切换导致的额外开销

## 2.EventLoop接口

​	事件循环：运行任务来处理连接的生命周期内发生的事件（是任何网络框架的基本功能）

​	**并发+网络编程**

​	一个Eventloop 对应一个永远不会变的Thread 驱动，任务的执行（runable、callback可以交给Eventloop ，一个

​	Eventloop可能会被用于多个channel 。Eventloop 的 EventLoopGroup parent(); 返回的池

​	**所有的I/O操作和事件都由已经分配的EventLoop的线程处理（早期出站并不是这样，下游事件的处理由调用线程处**

​	**理，这就不能保证多个线程在同一时刻不会同时访问出站事件，当出站事件触发入站事件（发生异常），那么会带**

​	**来上下文切换）**

## 3.任务调度

​	**延迟/定时/周期性的事件处理**

### 	1.jdk的任务调度  

​	java.util.concurrent.Executors

```java
// 创建一个 ScheduledExecutorService 用于调度命令在指定延迟后运行或周期性执行 使用指定corePoolSize线程
public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize)
public static ScheduledExecutorService newScheduledThreadPool( int corePoolSize, ThreadFactory threadFactory) 
 // 创建一个 ScheduledExecutorService 用于调度命令在指定延迟后运行或周期性执行   使用一个线程
public static ScheduledExecutorService newSingleThreadScheduledExecutor() 
public static ScheduledExecutorService newSingleThreadScheduledExecutor(ThreadFactory threadFactory)    
```

​	**缺点：线程管理不会进行伸缩**

### 	2.EventLoop的任务调度

```java
ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit);
ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit);
```

## 4.实现细节

### 1.线程管理

```java
boolean inEventLoop(Thread thread); //确定当前线程是否分配给当前的channel
```

一个channel 只能绑定一个 EventLoop 而一个 EventLoop  可以被多个 channel 所持有

Channel.envetLoop().execute(Task)  在代码执行时检查执行线程是否是分配的EventLoop的线程，若是则执行，否则放入

EventLoop的队列中，在稍后的时间片将执行该任务

**注意：不要阻塞I/O操作，因为将阻塞需要在该线程上执行的其他任务，必须阻塞时，建议使用一个专门的EventExecutor**

### 2.EventLoop/线程分配

根据不同的传输实现其实现也不同

1.异步传输（使用少量的线程/EventLoop ，也就是说EventLoop/线程会被多个 channel 共享,当然可以用ThradLocal）

2.阻塞传输 1：1 = EventLoop :channel

# 8.引导（组装）

## 1.Bootstrap -> AbstractBootstrap->Cloneable

　ServerBootstrap	-> AbstractBootstrap->Cloneable

## 2.引导客户端和无连接协议 -> Bootstrap类 

```java
//共有部分 与服务端一样的 B Bootstrap   C Channel
public B group(EventLoopGroup group) //设置 处理的EventLoopGroup
public B channel(Class<? extends C> channelClass) //用于指定 Channel的实现 默认调构造否则使用 
public B channelFactory(ChannelFactory<? extends C> channelFactory)//ChannelFactory的newChannel
public B localAddress(SocketAddress localAddress)//指定绑定的ip和端口 也可用 bind 或者 connect
public <T> B option(ChannelOption<T> option, T value)//设置ChannelOption，其将会被应用于新创建的													//channel
public <T> B attr(AttributeKey<T> key, T value) //设置channel的属性
public B handler(ChannelHandler handler) //添加 事件处理
public B clone() //创建一个浅拷贝 共享一个 EventLoopGroup
public ChannelFuture bind() //先 connect 后 bind 绑定Channel
//以上 均为共有    
public ChannelFuture connect() //连接到远程节点
public Bootstrap remoteAddress(SocketAddress remoteAddress)   // 设置远程地址 之后再 connect bind
```

客户端

```java
		NioEventLoopGroup eventExecutors = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        ChannelFuture connect = bootstrap.group(eventExecutors)
            .channel(NioSocketChannel.class)
            .handler(new SimpleChannelInboundHandler<ByteBuf>() {
                @Override
                protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {

                }
            }).connect(new InetSocketAddress("ip", 80));
        connect.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                System.out.println("成功连接");
            } else {
                System.out.println("失败连接");
                future.cause().printStackTrace();
            }
        });
```

**注意：Channel 与EventGroup 存在兼容性问题不可以混用**

​	**NioEventLoopGroup    NioDatagramChannel  NioServerSocketChannel NioSocketChannel**

​	**OioEventLoopGroup    OioDatagramChannel  OioServerSocketChannel  OioSocketChannel**

​	**在调用 connect 或者 bind 之前一定 group  channel/channelFactory  handler**

## 3.引导服务器

api 公用的就不说了 参考客户端的

```java
public <T> ServerBootstrap childOption(ChannelOption<T> childOption, T value) //指定子channel的属性
public <T> ServerBootstrap childAttr(AttributeKey<T> childKey, T value)  //将属性设置给已接收的子																		//channel 
public ServerBootstrap childHandler(ChannelHandler childHandler) //给接收的子channel 添加事件处理    
```

构建服务器   服务器的子channel 就是已经连接的客户端的channel

```java
	    NioEventLoopGroup group = new NioEventLoopGroup();
        ServerBootstrap server = new ServerBootstrap();
        ChannelFuture future = server.group(group)
            .channel(NioServerSocketChannel.class)
            .childHandler(new SimpleChannelInboundHandler<ByteBuf>() {
                @Override
                protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                    System.out.println("收到数据");
                }
            })
            .bind(new InetSocketAddress(9000));
        future.addListener((ChannelFutureListener) future1 -> {
            if (future.isSuccess()) {
                System.out.println("服务器绑定成功");
            } else {
                System.out.println("服务器绑定失败");
                future.cause().printStackTrace();
            }
        });
```

## 4.从Channel引导客户端 

​	服务器处理完的结果想发送给第三方

​	那么就可以在服务器端的子channel 共享出Eventloop，创建新的引导客户端

```java
        ServerBootstrap server = new ServerBootstrap();
        ChannelFuture future = server.group(new NioEventLoopGroup(), new NioEventLoopGroup())
            .channel(NioServerSocketChannel.class)
            .childHandler(new SimpleChannelInboundHandler<ByteBuf>() {
                ChannelFuture channelFuture;

                @Override
                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                    Bootstrap bootstrap = new Bootstrap();
                    channelFuture = bootstrap.group(ctx.channel().eventLoop())
                        .channel(NioSocketChannel.class)
                        .handler(new SimpleChannelInboundHandler<ByteBuf>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                                System.out.println("收到数据");
                            }
                        }).connect(new InetSocketAddress("ip", 9000));

                }

                @Override
                protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                    if (channelFuture.isDone()) {
                        //处理
                        System.out.println("收到数据");
                    }
                }
            })
            .bind(new InetSocketAddress(9000));
        future.addListener((ChannelFutureListener) future1 -> {
            if (future.isSuccess()) {
                System.out.println("服务器绑定成功");
            } else {
                System.out.println("服务器绑定失败");
                future.cause().printStackTrace();
            }
        });
```

## 5.在引导过程添加多个ChannelHandler

```java
public abstract class ChannelInitializer<C extends Channel>

//服务端
serverBootstrap.group(nioEventLoopGroup).channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(9000))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(echoServerHandler);
                    }
                });
//客户端
 bootstrap.group(eventExecutors).channel(NioSocketChannel.class)
                .remoteAddress("127.0.0.1", 9000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new EchoClientHandler());
                    }
                });
```

## 6.使用ChannelOption和属性

```java
        //线程不安全的
        //final AttributeKey<Integer> id = AttributeKey.newInstance("ID");
        //线程安全的
        final AttributeKey<Integer> id = AttributeKey.valueOf("ID");
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup())
            .channel(NioSocketChannel.class)
            .handler(new SimpleChannelInboundHandler<ByteBuf>() {
                @Override
                public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
                    System.out.println("注册成功");
                    Integer i = ctx.channel().attr(id).get();
                    System.out.println("id 是" + i);
                }
                @Override
                protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                    System.out.println("收到 数据");
                }
            });
        bootstrap.option(ChannelOption.SO_KEEPALIVE,true)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000);
        bootstrap.attr(id,123456);
        ChannelFuture connect = bootstrap.connect(new InetSocketAddress("", 9000));
        connect.syncUninterruptibly();
```

## 7.引导DatagramChannel

前面的都是基于TCP 协议，而 DatagramChannel 无协议的不再调用 connect 而是调用bind

## 8.关闭

关闭EvevtLoop 

```java
    //第一种 
	ChannelFuture future = bootstrap.connect(new InetSocketAddress("", 9000));
     future.syncUninterruptibly();
	//第二种
	Channel.close();
    EventLoopGroup.shutdownGracefully();
```

# 9.单元测试

​	EmbeddedChannel类  EmbeddedEventLoop

```java
public <T> T readInbound() //读取入站 该消息传递过整个channelpipeline
public <T> T readOutbound() //读取出站消息 该消息传递过整个channelpipeline
public boolean writeInbound(Object... msgs) //将入站信息 写回 channel  写入信息则true
public boolean writeOutbound(Object... msgs)  //将出站消息 写回channel  写入信息则true
public boolean finish() //将channel 标记完成 如果有可被读取的入站数据或者写数据 返回true 关闭channel   
```

## 测试入站

```java
@Test
    public void testInBound() {
        ByteBuf buffer = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buffer.writeByte(i);
        }
        ByteBuf duplicate = buffer.duplicate();
        EmbeddedChannel channel = new EmbeddedChannel(new FixLengthDecoder(3));
        //写入 9个 字节
        assertTrue(channel.writeInbound(duplicate.retain()));
        assertTrue(channel.finish());

        //第一次读
        ByteBuf read = (ByteBuf) channel.readInbound();
        assertEquals(buffer.readSlice(3), read);
        read.release();

        //第二次读
        read = (ByteBuf) channel.readInbound();
        assertEquals(buffer.readSlice(3), read);
        read.release();


        //第三次读
        read = (ByteBuf) channel.readInbound();
        assertEquals(buffer.readSlice(3), read);
        read.release();

        //最后
        assertNull(channel.readInbound());
        buffer.release();
    }
    @Test
    public void testInBound2() {
        ByteBuf buffer = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buffer.writeByte(i);
        }
        ByteBuf duplicate = buffer.duplicate();
        EmbeddedChannel channel = new EmbeddedChannel(new FixLengthDecoder(3));
        //写入分两次写入 两字节 不会 =true
        assertFalse(channel.writeInbound(duplicate.readBytes(2)));
        assertTrue(channel.writeInbound(duplicate.readBytes(7)));
        assertTrue(channel.finish());

        //第一次读
        ByteBuf read = (ByteBuf) channel.readInbound();
        assertEquals(buffer.readSlice(3), read);
        read.release();

        //第二次读
        read = (ByteBuf) channel.readInbound();
        assertEquals(buffer.readSlice(3), read);
        read.release();


        //第三次读
        read = (ByteBuf) channel.readInbound();
        assertEquals(buffer.readSlice(3), read);
        read.release();

        //最后
        assertNull(channel.readInbound());
        buffer.release();
    }
```

入站的定长解码器

```java
public class FixLengthDecoder extends ByteToMessageDecoder {
    private final int length;

    public FixLengthDecoder(int length) {
        if (length < 0) {
            throw new IllegalArgumentException("长度不能小于零");
        }
        this.length = length;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        while (in.readableBytes() >= length) {
            ByteBuf byteBuf = in.readBytes(length);
            out.add(byteBuf);
        }
    }
}
```

## 测试出站：

```java
    @Test
    public void test1() {
        ByteBuf buffer = Unpooled.buffer();
        for (int i = 1; i < 10; i++) {
            //注意 写入 writeInt
            buffer.writeInt(-i);
        }
        EmbeddedChannel channel = new EmbeddedChannel(new AbsIntegerEncoder());
        //写入
        assertTrue(channel.writeOutbound(buffer));
        assertTrue(channel.finish());
        //测试
        for (int i = 1; i < 10; i++) {
            assertEquals(i, (int) channel.readOutbound());
        }
        assertNull(channel.readOutbound());
    }
```

出站的编码器

```java
public class AbsIntegerEncoder extends MessageToMessageEncoder<ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        //Integer 4字节
        while (msg.readableBytes() >= 4) {
            int abs = Math.abs(msg.readInt());
            out.add(abs);
        }
    }
}
```

## 异常处理

```java
    @Test
    public void test1() {
        ByteBuf buffer = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buffer.writeByte(i);
        }
        ByteBuf duplicate = buffer.duplicate();
        EmbeddedChannel channel = new EmbeddedChannel(new MaxLengthDecoder(3));
        //写入两字节产生一个帧
        assertTrue(channel.writeInbound(duplicate.readBytes(2)));
        try {
            //写入4字节产生一个帧
            assertFalse(channel.writeInbound(duplicate.readBytes(4)));
            Assert.fail();
        } catch (TooLongFrameException e) {
            //异常处理
        }
        //写入剩余的字节
        assertTrue(channel.writeInbound(duplicate.readBytes(3)));
        assertTrue(channel.finish());

        //读取写入的信息
        ByteBuf readInbound = channel.readInbound();
        //验证前两字节
        assertEquals(buffer.readSlice(2), readInbound);
        readInbound.release();

        //验证后 3字节
        readInbound = channel.readInbound();
        assertEquals(buffer.skipBytes(4).readSlice(3), readInbound);
        readInbound.release();
        buffer.release();
    }
```

最长验证解码器

```java
/**
 * 对 入站信息 解码 按指定帧的最大长度
 * @author Ryze
 * @date 2018-12-06 9:47
 */
public class MaxLengthDecoder extends ByteToMessageDecoder {
    private final int length;

    public MaxLengthDecoder(int length) {
        if (length < 0) {
            throw new IllegalArgumentException("长度不能小于零");
        }
        this.length = length;
    }

    @Override
protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int readableBytes = in.readableBytes();
        //超过指定帧的大小则丢弃，防止资源耗尽
        if (readableBytes > length) {
            in.clear();
            throw new TooLongFrameException();
        }
        ByteBuf byteBuf = in.readBytes(readableBytes);
        out.add(byteBuf);
    }
}
```

# 10.编解码器

将网络格式（原始的字节序列）转换成应用程序的数据   解码  反过来就是编码

## 1.解码器 decode

### 	1.字节 -> 消息  ByteToMessageDecoder   和 ReplayingDecoder

​	***ByteToMessageDecoder***   

```java
//为每个 in 操作 到 out
protected abstract void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception
//简单调用 decode ，当channel 变得非活动时，这个方法会被调用一次，可以重新该方法提供特殊的处理
protected void decodeLast(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception
/**
 * 转成int 的解码器
 * @author Ryze
 * @date 2018-12-06 11:28
 */
public class ToIntegerDecoder extends ByteToMessageDecoder {
    @Override
protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        while (in.readableBytes() >= 4) {
            out.add(in.readInt());
        }
    }
}
```

​	**注意：一旦消息被编码或者解码那么就会被 ReferenceCountUtil.release(msg) 调用自动释放，如果想保留引用以**

​	**便之后使用，那么需要的 ReferenceCountUtil.retain(msg)**

​	***ReplayingDecoder***	

```java
// ReplayingDecoder 扩展了 ByteToMessageDecoder 使得我们不必调用 while (in.readableBytes() >= 4)  
// 这样的代码     S 用于状态管理 Void 是不需要状态管理  其实就是是用了个自定义的ByteBuf
public abstract class ReplayingDecoder<S> extends ByteToMessageDecoder
/**
 * 转成int 的解码器
 * @author Ryze
 * @date 2018-12-06 11:43
 */
public class ToIntegerDecoder2 extends ReplayingDecoder<Void> {
@Override
protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //当这步没有足够字节读取则会抛出Error（Signal 会被基类所捕获） 当有足够数据读取又会被调用
        out.add(in.readInt());
    }
}
```

​	注意：1.不是所用的ByteBuf 的操作都被支持（否则就是UnsupportedOperationException）

​		    2.速度稍慢于ByteToMessageDecoder

​	所以当 ByteToMessageDecoder不会引入很多复杂性则用 ByteToMessageDecoder否则ReplayingDecoder

### 	2.消息 -> 另外一种消息   MessageToMessageDecoder

```java
// I 表示接收的类型
public abstract class MessageToMessageDecoder<I> extends ChannelInboundHandlerAdapter
//同上的解码
protected abstract void decode(ChannelHandlerContext ctx, I msg, List<Object> out) throws Exception
/**
 * int -> String
 * @author Ryze
 * @date 2018-12-06 13:49
 */
public class IntegerToString extends MessageToMessageEncoder<Integer> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Integer msg, List<Object> out) throws Exception {
        out.add(String.valueOf(msg));
    }
}
```

### 	3.帧过大的处理

```java
public class TooLongFrameException extends DecoderException 
```

## 2.编码器 encode

### 	1.消息 -> 字节 MessageToByteEncoder

```java
// 处理的 msg 结果集out 
protected abstract void encode(ChannelHandlerContext ctx, I msg, ByteBuf out) throws Exception;

/**
 * short -> Byte
 * @author Ryze
 * @date 2018-12-06 14:01
 */
public class ShortToByteEncoder extends MessageToByteEncoder<Short> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Short msg, ByteBuf out) throws Exception {
        out.writeShort(msg);
    }
}
```

### 	2. 消息 ->消息 MessageToMessageEcoder	 

```java
/**
 * int ->String
 * @author Ryze
 * @date 2018-12-06 14:05
 */
public class IntegerToStringEncoder extends MessageToMessageEncoder<Integer> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Integer msg, List<Object> out) throws Exception {
        out.add(String.valueOf(msg));
    }
}
```

## 3.抽象的编解码器类

### ByteToMessageCodec

```java
//编码
protected abstract void encode(ChannelHandlerContext ctx, I msg, ByteBuf out) throws Exception;
//解码
protected abstract void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception;
//解码最后的追加
protected void decodeLast(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception 	
```

### MessageToMessageCodec<INBOUND_IN, OUTBOUND_IN>

```java
//解码
protected abstract void decode(ChannelHandlerContext ctx, INBOUND_IN msg, List<Object> out)
            throws Exception;
//编码
protected abstract void encode(ChannelHandlerContext ctx, OUTBOUND_IN msg, List<Object> out)
            throws Exception;
//示例 就是webSocket协议的解析
```

### CombinedChannelDuplexHandler 编解码组合器

```java
public class CombinedChannelDuplexHandler<I extends ChannelInboundHandler, O extends ChannelOutboundHandler> extends ChannelDuplexHandler
```

# 11.预置的ChannelHandler和编解码器

## 1.通过SSL/TLS (传输层安全协议)保护netty 程序

java 的 javax.net.ssl 包 SSLContext SSLEngine 实现解码/编码

netty 的SslHandler 实现了这个功能

netty 根据OpenSsl工具包（www.openssl.org）的 SSLEngine 实现了更好性能的实现。（具体配置 去百度）

添加支持：

```java
/**
 * 添加 ssl的支持
 * @author Ryze
 * @date 2018-12-06 15:48
 */
public class SslChannelInitializer extends ChannelInitializer<Channel> {
    private final SslContext sslContext;
    private final boolean startTls;

    /**
     * @param sslContext 要的 sslContext
     * @param startTls 为true 第一个写入的不会被加密  客户端应该设为true
     */
    public SslChannelInitializer(SslContext sslContext, boolean startTls) {
        this.sslContext = sslContext;
        this.startTls = startTls;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        SSLEngine sslEngine = sslContext.newEngine(ch.alloc());
        ch.pipeline().addFirst("ssl", new SslHandler(sslEngine, startTls));
    }
}
```

​	SslHandler 的api

```java
//获取或者设置超时时间
public long getHandshakeTimeoutMillis()
public void setHandshakeTimeout(long handshakeTimeout, TimeUnit unit)
public void setHandshakeTimeoutMillis(long handshakeTimeoutMillis)
// 过期的方法 设置或者获取超时时间，超时之后将触发一个关闭通知，并关闭连接    
public long getCloseNotifyTimeoutMillis()
public void setCloseNotifyTimeout(long closeNotifyTimeout, TimeUnit unit) 
public void setCloseNotifyTimeoutMillis(long closeNotifyFlushTimeoutMillis)
 //将在当前TLS握手完成后收到通知   
public Future<Channel> handshakeFuture() 
 //过期方法 请求关闭并销毁底层的SslEngine  
public ChannelFuture close()
public ChannelFuture close(ChannelPromise promise)    
```

## 2.基于netty构建http/https 应用

### 1.普通的http的实现

http 请求：httpRequest   head+content+content+...+LastContent  ---- HttpRequestDecoder  HttpRequestEncoder

http 响应：httpReponse   head+content+content+...+LastContent  ---- HttpResponseDecoder  HttpResponseEncoder

```java
/**
 * 支持 http
 * @author Ryze
 * @date 2018-12-06 16:13
 */
public class HttpHandler extends ChannelInitializer<Channel> {
    private final boolean client;

    public HttpHandler(boolean client) {
        this.client = client;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //如果是客户端 请求加密 回应解析
        if (client) {
            pipeline.addLast("decoder", new HttpResponseDecoder())
                .addLast("encoder", new HttpRequestEncoder());
        } else {
            //如果是服务端 请求解析 回应加密
            pipeline.addLast("decoder", new HttpRequestDecoder())
                .addLast("encoder", new HttpResponseEncoder());
        }
    }
}

/**
 * http 消息聚合
 * @author Ryze
 * @date 2018-12-06 16:22
 */
public class HttpAggregationInitializer extends ChannelInitializer<Channel> {
    private final boolean isClient;

    public HttpAggregationInitializer(boolean isClient) {
        this.isClient = isClient;
    }
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //如果是客户端
        if (isClient) {
            pipeline.addLast("codec", new HttpClientCodec());
        } else {
            pipeline.addLast("codec", new HttpServerCodec());
        }
        //将最大消息为 512K的 HttpObjectAggregator 添加到 pipeline
        //注意 ： HttpObjectAggregator 必须放在编解码 之后
        pipeline.addLast("aggregation", new HttpObjectAggregator(512 * 1024));
    }
}
/**
 * http 压缩  一般都是客户端压缩
 *            服务端压缩则客户端需要支持
 * @author Ryze
 * @date 2018-12-06 16:29
 */
public class HttpCompressionInitializer extends ChannelInitializer<Channel> {
    private final boolean isClient;

    public HttpCompressionInitializer(boolean isClient) {
        this.isClient = isClient;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //如果是客户端
        if (isClient) {
            pipeline.addLast("codec", new HttpClientCodec())
                //解压
                .addLast("decompression", new HttpContentDecompressor());
        } else {
            pipeline.addLast("codec", new HttpServerCodec())
                //压缩
                .addLast("compression", new HttpContentCompressor());
        }
    }
}

/**
 * 添加 https的支持
 * @author Ryze
 * @date 2018-12-06 15:48
 */
public class HttpsChannelInitializer extends ChannelInitializer<Channel> {
    private final SslContext sslContext;
    private final boolean isClient;

    /**
     * @param sslContext 要的 sslContext
     * @param isClient 是不是客户端
     */
    public HttpsChannelInitializer(SslContext sslContext, boolean isClient) {
        this.sslContext = sslContext;
        this.isClient = isClient;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        SSLEngine sslEngine = sslContext.newEngine(ch.alloc());
        ChannelPipeline pipeline = ch.pipeline().addFirst("ssl", new SslHandler(sslEngine));
        //如果是客户端
        if (isClient) {
            pipeline.addLast("codec", new HttpClientCodec());
        } else {
            pipeline.addLast("codec", new HttpServerCodec());
        }
    }
}
```

### 2.基于WebSocket的http实现

 普通http与WebSocket的区别：

​	普通的http请求是响应式的即 请求 --应答 完事

​	而websocket 通过第一次的http(s)的握手之后，打通了双向通道（协议升级并维持 ping pong）。

```java
/**
 * WebSocketServer 服务端
 * @author Ryze
 * @date 2018-12-06 16:54
 */
public class WebSocketServerInitializer extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
            //编码
            .addLast(new HttpServerCodec())
            //聚合数据
            .addLast(new HttpObjectAggregator(65535))
            //握手的处理端点
            .addLast(new WebSocketServerProtocolHandler("/websocket"))
            //数据帧是文本数据的处理
            .addLast(new TextFrameHandler())
            //数据帧是二进制的处理
            .addLast(new BinaryFrameHandler())
            //数据帧 属于上一个 TextWebSocketFrame或者 BinaryWebSocketFrame的数据 处理
            .addLast(new ContinuationWebSocketFrameHandler());

    }

    /**
     * 数据帧是文本数据的处理
     */
    private class TextFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
            //处理
        }
    }

    /**
     * 数据帧是二进制的处理
     */
    private class BinaryFrameHandler extends SimpleChannelInboundHandler<BinaryWebSocketFrame> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, BinaryWebSocketFrame msg) throws Exception {
            //处理
        }
    }


    /**
     * 数据帧 属于上一个 TextWebSocketFrame或者 BinaryWebSocketFrame的数据
     */
    private class ContinuationWebSocketFrameHandler extends SimpleChannelInboundHandler<ContinuationWebSocketFrame> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ContinuationWebSocketFrame msg) throws Exception {
            //处理
        }
    }
}
```

还有三个控制帧处理：

 CloseWebSocketFrame PingWebSocketFrame PongWebSocketFrame

## 3.处理空闲的连接和超时

​	**检测空闲连接以及超时连接对于及时释放资源至关重要**

​	IdleStateHandler : 处理空闲

​	ReadTimeoutHandler：处理读超时

​	WriteTimeoutHandler ：处理写超时

```java
/**
 * 处理闲置
 * @author Ryze
 * @date 2018-12-06 17:21
 */
public class IdleStateHandlerInitializer extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
            //超时之后会触发一个 IdleStateEvent 事件
            .addLast(new IdleStateHandler(0, 0, 60, TimeUnit.SECONDS))
            .addLast(new HeartbeatHandler());

    }

    private static class HeartbeatHandler extends ChannelInboundHandlerAdapter {
        private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(
            "HEARTBEAT", CharsetUtil.ISO_8859_1
        ));

        /**
         * 对事件进行处理
         */
        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                //发送一个心跳  失败的话 关闭连接
                ctx.writeAndFlush(HEARTBEAT_SEQUENCE.duplicate())
                    .addListener(ChannelFutureListener.CLOSE);
            }else {
                super.userEventTriggered(ctx,evt);
            }
        }
    }
}
```

​	

## 4.解码基于分隔符的协议和基于长度的协议

### 1.基于分隔符的协议

public class DelimiterBasedFrameDecoder 基于用户自定义分隔符提取帧的通用解码器

public class DelimiterBasedFrameDecoder 基于\n 或者\r\n的分隔符提取帧的解码器（更快）

```java
/**
 * 处理 行尾是  \n 或者\r\n的分隔符
 * @author Ryze
 * @date 2018-12-06 17:45
 */
public class LineBaseHandlerInitializer extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
            .addLast(new LineBasedFrameDecoder(64 * 1024))
            .addLast(new FrameHandler());

    }

    private static class FrameHandler extends SimpleChannelInboundHandler<ByteBuf> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
            //在这里 可以接收 完整的帧
        }
    }
}
/**
 * 1.数据流 由一系列的帧组成 以\n 尾分隔符
 * 2.每个帧 由一系列的命令元素组成 以' '分隔
 * 3.一个帧 代表一个命令 定义为 一个命令+数目可变的参数
 * Cmd 将帧（命令）的内容存在ByteBuf 中，一个ByteBuf 用于名称 另一个用于参数
 * CmdDecoder 从被重写的decode() 方法获取一行字符串 并将它的内容构建一个Cmd 实例
 * CmdHandler 从上一步的Cmd对象进行一系列处理
 * @author Ryze
 * @date 2018-12-06 17:53
 */
public class MyDelimiterBasedFrameInitializer extends ChannelInitializer<Channel> {
    final byte SPACE = (byte) ' ';

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
            .addLast(new CmdDecoder(6 * 1024))
            .addLast(new CmdHandler());
    }

    private class CmdDecoder extends LineBasedFrameDecoder {
        public CmdDecoder(int i) {
            super(i);
        }

        @Override
        protected Object decode(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
            //1 解决
            ByteBuf decode = (ByteBuf) super.decode(ctx, buffer);
            if (decode == null) {
                return null;
            }
            //2.解决
            int i = decode.indexOf(decode.readerIndex(), decode.writerIndex(), SPACE);
            return new Cmd(decode.slice(decode.readerIndex(), i), decode.slice(i + 1, decode.writerIndex()));
        }
    }

    private class CmdHandler extends SimpleChannelInboundHandler<Cmd> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Cmd msg) throws Exception {
            // cmd 处理
        }
    }
    private class Cmd {
        private ByteBuf name;
        private ByteBuf args;

        public ByteBuf getName() {
            return name;
        }

        public ByteBuf getArgs() {
            return args;
        }

        public Cmd(ByteBuf name, ByteBuf args) {
            this.name = name;
            this.args = args;
        }
    }
}
```

### 2.基于长度的协议

​	FixedLengthFrameDecoder 根据指定的长度（构造参数）提取的定长帧

​	LengthFieldBasedFrameDecoder 根据编码进帧头部长度提取帧

```java
/**
 * 根据头字节写的长度 提取帧
 * @author Ryze
 * @date 2018-12-06 18:30
 */
public class LengthFieldBasedDecoderInitializer extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
            //前八字节表示长度
            .addLast(new LengthFieldBasedFrameDecoder(64 * 1024, 0, 8))
            //提取帧 之后的处理
            .addLast(new FramHandler());

    }

    private class FramHandler extends SimpleChannelInboundHandler<ByteBuf> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
            //处理
        }
    }
}
```

## 5.写入大型数据

### 1.zero copy

```java
/**
 * zero copy 只适合传输 不适合处理
 * @author Ryze
 * @date 2018-12-06 18:40
 */
public class ZeroCopy {
    public static void main(String[] args) {
        try {
            File file = new File("文件地址");
            FileInputStream in = new FileInputStream(file);
            DefaultFileRegion defaultFileRegion = new DefaultFileRegion(in.getChannel(), 0, file.length());
            Channel channel = new NioServerSocketChannel();
            channel.writeAndFlush(defaultFileRegion)
                .addListener(future -> {
                    if (!future.isSuccess()) {
                        //异常处理
                        future.cause().printStackTrace();
                    }
                });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
```

### 2.ChunkedInput  < B > 接口

​	ChunkedFile   //从文件逐块获取数据 当不支持零拷贝或者需要处理数据

​	ChunkedNioFile  // 从FileChannel逐块获取数据

​	ChunkedNioStream  // 从 准备好读的 channel 逐块获取数据

​	ChunkedStream / /从InpuStream逐块获取数据

```java
/**
 * 逐块 写出
 * @author Ryze
 * @date 2018-12-06 18:56
 */
public class ChunkedWriteHandlerInitializer extends ChannelInitializer<Channel> {
    private final File file;
    private final SslContext sslContext;

    public ChunkedWriteHandlerInitializer(File file, SslContext sslContext) {
        this.file = file;
        this.sslContext = sslContext;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
            .addLast(new SslHandler(sslContext.newEngine(ch.alloc())))
            //注意 必须的
            .addLast(new ChunkedWriteHandler())
            .addLast(new WriteStreamHandler());
    }

    private class WriteStreamHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            super.channelActive(ctx);
            // 以上 四个均适用
            ctx.writeAndFlush(new ChunkedStream(new FileInputStream(file)));
        }
    }
}
```

## 6.序列化数据

### 1.JDK的序列化

​	CompatibleObjectEncoder

​	ObjectDecoder

​	ObjectEncoder

### 2.JBoss Marshalling 序列化 （比jdk 快3倍 前两个兼容jdk 性能差）

​	CompatibleMarshallingDecoder

​	CompatibleMarshallingEncoder

​	MarshallingDecoder

​	MarshallingEncoder

### 3.Protocol Buffers 序列化

​	ProtobufDecoder

​	ProtobufEncoder

​	ProtobufVarint32FrameDecoder  动态解析

​	ProtobufVarint32LengthFieldPrepender  动态解析

```java
/**
 * 谷歌的序列化
 * @author Ryze
 * @date 2018-12-07 0:27
 */
public class ProtoBufInitializer extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
            .addLast(new ProtobufVarint32FrameDecoder())
            .addLast(new ProtobufEncoder())
            .addLast(new ProtobufDecoder(new MessageLite()));
    }
}
```

# 12.WebSocket

旨在为web上的双向数据传输问题提供一个切实可行的解决方案，使得服务器与客户端之间可以在任意时刻传输

从http(s)到websocket 需要一次http（s）的握手，之后会进行升级

websocket的帧处理

```java
public class BinaryWebSocketFrame extends WebSocketFrame  包含二进制数据
public class TextWebSocketFrame extends WebSocketFrame  包含文本数据
public class ContinuationWebSocketFrame extends WebSocketFrame 包含二进制 或者 文本数据
public class CloseWebSocketFrame extends WebSocketFrame 表示关闭请求 一个关闭状态和关闭原因
public class PingWebSocketFrame extends WebSocketFrame  请求发送个PongWebSocketFrame
public class PongWebSocketFrame extends WebSocketFrame  响应 PingWebSocketFrame
```

例子：聊天室

# 13.使用UDP广播事件

tcp 管理两个连接点之间在生命周期内进行有序可靠地消息传递，以及连接有序的终止  类似 打电话

udp 类似于 邮寄明信片   顺序无法保证  到不到达目的地也不能确定  （但是很快 纠错机制，消息管理机制 握手等全部

消除）

```java
//定义了一个消息 包装了 M 消息类型  A地址类型
public interface AddressedEnvelope<M, A extends SocketAddress> extends ReferenceCounted
//以上的默认实现
public class DefaultAddressedEnvelope<M, A extends SocketAddress> implements AddressedEnvelope<M, A>
//扩展了默认实现 使用ByteBuf 作为消息数据容器    
public final class DatagramPacket
        extends DefaultAddressedEnvelope<ByteBuf, InetSocketAddress> implements ByteBufHolder
//扩展了netty的channel 以支持UDP的多播组管理   简单的消息容器（接收者的地址和消息）     
public interface DatagramChannel extends Channel
//定义了一个能够发送和接收AddressedEnvelope消息的Channel类型
public final class NioDatagramChannel
        extends AbstractNioMessageChannel implements io.netty.channel.socket.DatagramChannel
```

例子：udp 广播日志

# 14.案例研究

### 1.内部基础设施

droplr  （一个实时上传服务）

接收文件(服务器)->上传文件服务器->文件（如果是图片生成缩略图）->应答客户端

**永远不要在netty的I/O线程上执行人合肥CPU限定的代码，你将会从netty偷取宝贵的资源，影响服务器的吞吐**

Firebase（实时数据同步服务）

基于netty的流水线处理和特殊的数据提供指定的handler 可以做到性能极大提高的兼容各种服务（http websocket 等）

当然也兼容了浏览器

urban airship

兼容不同的自定义协议，管理大量的并发连接

### 2.设计框架和服务 以满足极端伸缩性以及可扩展性

facebook

Thrift->Nifty->Swift

Twitter

finagle





​	



​	

## 	

​	

​	

​	

​		

​	

​	