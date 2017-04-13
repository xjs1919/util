package com.github.xjs.util.delay;

import com.github.xjs.util.delay.DelayedService.OnDelayedListener;
import com.github.xjs.util.delay.DelayedService.OnStartListener;

public class DelayedServiceTest {
	
	public static class DelayedOrder extends BaseDelayed<String>{
		public DelayedOrder(int timeout, String orderId) {
			super(timeout, orderId);
		}
		public String getOrderId(){
			return super.getValue();
		}
	}
	
	public static void main(String[] args) {
		DelayedService service = new DelayedService();
		service.start(new OnStartListener(){
			@Override
			public void onStart() {
				System.out.println("启动完成");
			}
		}, 
		new OnDelayedListener(){
			@Override
			public <T extends BaseDelayed<?>> void onDelayedArrived(T delayed) {
				System.out.println("[onDelayedArrived]"+delayed.toString());
			}
		});
		service.add(new DelayedOrder(60,"66666666"));
		service.add(new DelayedOrder(20,"2222222222"));
		service.add(new DelayedOrder(10,"1111111111"));
	}
}
