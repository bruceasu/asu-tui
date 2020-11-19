import io.spring.IResource;
import io.spring.PathMatchingResourcePatternResolver;
import java.io.IOException;

public class TestSomething
{
    public static void main(String[] args) throws IOException
    {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        IResource[] resource = resolver.getResources("classpath*:/linux/*.exe");
        if (resource != null) {
            for (IResource r : resource) {
                System.out.println("r = " + r);
            }
        } else {
            System.out.println("Found nothing");
        }
    }


}
