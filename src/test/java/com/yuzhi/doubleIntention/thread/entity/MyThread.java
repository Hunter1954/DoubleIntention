/**  
* <p>Title: MyThread.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2019</p>  
* <p>Company: www.yuzhi.com</p>  
* @author Hunter  
* @date 2019年6月27日上午10:50:53  
* @version 1.0  
*/  
package com.yuzhi.doubleIntention.thread.entity;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;

/**  
* <p>Title: MyThread</p>  
* <p>Description: </p>  
* @author Hunter  
* @date 2019年6月27日  
*/
public class MyThread extends Thread{
	/* (non-Javadoc)  
	 * <p>Title: run</p>  
	 * <p>Description: </p>    
	 * @see java.lang.Thread#run()  
	 */
	
	@Autowired
	SimulateService simulateService;
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		try {
			for (int i = 0; i < 10; i++) {
				int time=(int)Math.random()*100000000;
				Thread.sleep(time);
				System.out.println("run="+Thread.currentThread().getName());	
				simulateService.doubleIntentionProcess(i+"我系钱咬春，系兄弟，就来贪玩蓝月！"+Thread.currentThread().getName(), new Random().nextInt(3)+1);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
