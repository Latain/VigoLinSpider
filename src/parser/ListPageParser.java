package parser;
import java.util.List;
import entity.Page;
public abstract class ListPageParser implements Parser {
	public abstract List<String> parse(Page page);
}
