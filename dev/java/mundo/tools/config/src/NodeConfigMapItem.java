import java.io.PrintWriter;

public class NodeConfigMapItem extends NodeConfigItem
{
  public NodeConfigMapItem(String k)
  {
    key = k;
  }
  @Override
  public void writeXML(XMLWriter w)
  {
    w.openTag("<"+key+" xsi:type=\"map\"");
    if (attrs != null)
      for (String[] a : attrs)
        w.print(" " + a[0] + "=\"" + a[1] + "\"");
    w.println(">");
    for (NodeConfigItem item : children)
      item.writeXML(w);
    w.closeTag("</"+key+">");
  }
}