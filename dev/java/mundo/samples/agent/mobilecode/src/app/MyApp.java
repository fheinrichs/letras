package app;

import org.mundo.rt.Mundo;
import org.mundo.rt.Service;
import org.mundo.agent.Agent;
import org.mundo.agent.DoIMobility;

import api.DoIMyAgent;

public class MyApp
{
  public static void main(String args[])
  {
    Mundo.init();
    try
    {
      Service client = new Service();
      Mundo.registerService(client);
	  
      DoIMobility mobility = Agent.newInstance(client.getSession(), "agent.MyAgent");
      DoIMyAgent agent = new DoIMyAgent(mobility);
      
      agent.sayHello();
      
      mobility.moveTo("server1");
      agent.sayHello();
	  
      mobility.moveTo("master");
      agent.sayHello();
    }
    catch(Exception x)
    {
      x.printStackTrace();
    }
    Mundo.shutdown();
  }
}
