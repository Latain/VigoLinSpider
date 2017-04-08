package parser;
import entity.Page;
import entity.Film;
public abstract class DetailPageParser implements Parser {
	public abstract Film parse(Page page);
}
