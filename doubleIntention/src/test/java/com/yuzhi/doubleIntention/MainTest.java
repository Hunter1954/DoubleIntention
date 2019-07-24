/**  
* <p>Title: MainTest.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2019</p>  
* <p>Company: www.yuzhi.com</p>  
* @author Hunter  
* @date 2019年5月28日上午9:42:41  
* @version 1.0  
*/  
package com.yuzhi.doubleIntention;


import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;


/**  
* <p>Title: MainTest</p>  
* <p>Description: </p>  
* @author Hunter  
* @date 2019年5月28日  
*/

public class MainTest implements CommandLineRunner{
	 Logger log = LoggerFactory.getLogger(getClass());
	
	/**  
	 * <p>Title: main</p>  
	 * <p>Description: </p>  
	 * @param args  
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		SpringApplication.run(YuzhiDoubleIntentionApplication.class, args);
		List<Integer> intList=new ArrayList<Integer>();
		List<Integer> intList2=new ArrayList<Integer>();
		intList2.add(-1);
		intList2.add(-2);
		intList.add(1);
		intList.add(3);
		intList.add(5);
		intList.add(6);
		intList.add(7);
		intList.remove(3);
		intList.addAll(5,intList2);
		for (int i = 0; i < intList.size(); i++) {
			System.out.println(intList.get(i));
		}
	}

	/* (non-Javadoc)  
	 * <p>Title: run</p>  
	 * <p>Description: </p>  
	 * @param args
	 * @throws Exception  
	 * @see org.springframework.boot.CommandLineRunner#run(java.lang.String[])  
	 */
	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub


	}

	
	
	
}
