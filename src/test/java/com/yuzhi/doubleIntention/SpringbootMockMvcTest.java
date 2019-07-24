/**  
* <p>Title: SpringbootMockMvcTest.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2019</p>  
* <p>Company: www.yuzhi.com</p>  
* @author Hunter  
* @date 2019年7月2日下午6:44:32  
* @version 1.0  
*/  
package com.yuzhi.doubleIntention;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

/**  
* <p>Title: SpringbootMockMvcTest</p>  
* <p>Description: </p>  
* @author Hunter  
* @date 2019年7月2日  
*/
@RunWith(SpringRunner.class)
@SpringBootTest(classes=YuzhiDoubleIntentionApplication.class)
@AutoConfigureMockMvc
public class SpringbootMockMvcTest {
	@Autowired
	private MockMvc mockMvc;
	
	@Test
	public void testRequest() throws Exception{
		MvcResult perform = mockMvc.perform(MockMvcRequestBuilders.get("/test")).andReturn();
		System.out.println(perform.getResponse());
	}
	
}
