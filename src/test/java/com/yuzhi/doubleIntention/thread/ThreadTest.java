/**  
* <p>Title: ThreadTest.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2019</p>  
* <p>Company: www.yuzhi.com</p>  
* @author Hunter  
* @date 2019年6月27日上午10:50:08  
* @version 1.0  
*/  
package com.yuzhi.doubleIntention.thread;

import org.springframework.beans.factory.annotation.Autowired;

import com.yuzhi.doubleIntention.thread.entity.MyThread;
import com.yuzhi.doubleIntention.thread.entity.SimulateService;
import com.yuzhi.doubleIntention.thread.service.impl.SimulateServiceImpl;

/**  
* <p>Title: ThreadTest</p>  
* <p>Description: </p>  
* @author Hunter  
* @date 2019年6月27日  
*/
public class ThreadTest {
	@Autowired
	static SimulateService simulateService;
	public static void main(String[] args) {
		MyThread thread =new MyThread();
		thread.setName("myThread");
		thread.start();
		try {
			for (int i = 0; i < 10; i++) {
				int time=(int)Math.random()*10000;
				Thread.sleep(time);
				System.out.println("main="+Thread.currentThread().getName());
				simulateService.doubleIntentionProcess(i+"大噶好，我系渣渣辉，系兄弟就来贪玩蓝月，杰西尼没有玩过的船新版本！"+Thread.currentThread().getName(), time);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
