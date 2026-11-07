
package com.careertrack;

import com.google.cloud.firestore.Firestore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class CareerTrackApplicationTests {

	// テスト時は実際のFirestoreに接続せず、モックを利用する
	@MockitoBean
	private Firestore firestore;

	@Test
	void contextLoads() {
	}

}
