package Server;


import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class UseJedisPool {

    private static JedisPool jedisPool = null;

    //初始化redis连接池
    static{
        JedisPoolConfig config = new JedisPoolConfig();
        //配置最大jedis实例数
        config.setMaxTotal(80);
        //配置资源池最大闲置数
        config.setMaxIdle(200);
        //等待可用连接的最大时间
        config.setMaxWaitMillis(5);
        //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的
        config.setTestOnBorrow(true);
        jedisPool = new JedisPool(config,"localhost",6379);
    }

    //获取Jedis实例
    public synchronized static Jedis getJedis(){
        if(jedisPool != null){
            System.out.println("请求jedis");
            Jedis resource = jedisPool.getResource();
            System.out.println("请求jedis完成"+resource);

            return resource;
        }else{
            return null;
        }
    }

    //释放Jedis资源
    public static void returnResource(final Jedis jedis){
        if(jedis != null){
            jedis.close();
//            jedisPool.returnResource(jedis);
        }
    }


    public static void main(String[] args){
        Jedis jedis = UseJedisPool.getJedis();
        jedis.set("test", "hello JedisPool.");
        jedis.set("hello", "JedisPool.");
        System.out.println(jedis.get("test"));
        UseJedisPool.returnResource(jedis);
        //测试发现释放Jedis资源后，下面的这个还能返回JedisPool  ????
        System.out.println(jedis.get("hello"));
    }
}