/**  
* <p>Title: SpringbootJunitTest.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2017</p>  
* <p>Company: www.baidudu.com</p>  
* @author Hunter  
* @date 2019年5月6日上午10:40:38  
* @version 1.0  
*/  
package com.yuzhi.doubleIntention;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * @author Hunter
 *
 */
@RunWith(SpringRunner.class)//底层使用的是SpringJUnit4ClassRunner
@SpringBootTest(classes=YuzhiDoubleIntentionApplication.class)//启动整个springboot工程
public class SpringbootJunitTest {
	@Test
	public void testReuqest1() {
		
		
		
		
	}
	
	
	
	
	
}
