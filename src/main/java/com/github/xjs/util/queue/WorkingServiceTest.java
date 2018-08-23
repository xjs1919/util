package com.github.xjs.util.queue;

public class WorkingServiceTest {
	
	public static class PushRequest implements QueueAble{
		private int id;
		private String msg;
		public PushRequest(){
		}
		public PushRequest(int id, String msg){
			this.id = id;
			this.msg = msg;
		}
		public int getId(){
			return this.id;
		}
		public String getMsg(){
			return this.msg;
		}
	}
	
	public static void main(String[] args) {
		WorkingService<PushRequest> ws = new WorkingService<PushRequest>();
		ws.start();
		for(int i=0;i<10;i++){
			ws.execute(new PushRequest(i, "this is a message"), new Callback<PushRequest>(){
				@Override
				public void callback(PushRequest request) {
					try{
						Thread.sleep(1000);
					}catch(Exception e){
					}
					System.out.println("handle message:"+request.getId());
				}
			});
		}
		System.out.println("over");
	}
}
