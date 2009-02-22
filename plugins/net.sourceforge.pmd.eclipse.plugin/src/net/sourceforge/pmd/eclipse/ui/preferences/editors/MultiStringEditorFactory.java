package net.sourceforge.pmd.eclipse.ui.preferences.editors;

import java.util.List;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.preferences.br.ValueChangeListener;
import net.sourceforge.pmd.lang.rule.properties.PropertyDescriptorWrapper;
import net.sourceforge.pmd.lang.rule.properties.StringMultiProperty;
import net.sourceforge.pmd.util.CollectionUtil;
import net.sourceforge.pmd.util.StringUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author Brian Remedios
 */
public class MultiStringEditorFactory extends AbstractMultiValueEditorFactory {

    public static final MultiStringEditorFactory instance = new MultiStringEditorFactory();
		
	private MultiStringEditorFactory() { }
	
    private static StringMultiProperty multiStringPropertyFrom(PropertyDescriptor<?> desc) {
	        
        if (desc instanceof PropertyDescriptorWrapper) {
           return (StringMultiProperty) ((PropertyDescriptorWrapper<?>)desc).getPropertyDescriptor();
        } else {
            return (StringMultiProperty)desc;
        }
    }
	
    protected Control addWidget(Composite parent, Object value, PropertyDescriptor<?> desc, Rule rule) {
        Text textWidget = new Text(parent, SWT.SINGLE | SWT.BORDER);
        setValue(textWidget,value);
        return textWidget;
    }    
    
    protected void setValue(Control widget, Object value) {
        ((Text)widget).setText(value == null ? "" : value.toString());
    }
    
    protected void configure(final Text textWidget, final PropertyDescriptor<?> desc, final Rule rule, final ValueChangeListener listener) {
                
        final StringMultiProperty smp = multiStringPropertyFrom(desc);
        
        textWidget.addListener(SWT.FocusOut, new Listener() {
        	public void handleEvent(Event event) {
        		String[] newValues = textWidgetValues(textWidget);					
        		String[] existingValues = rule.getProperty(smp);				
        		if (CollectionUtil.areSemanticEquals(existingValues, newValues)) return;				
        		
        		rule.setProperty(smp, newValues);        		
        		fillWidget(textWidget, desc, rule);    // reload with latest scrubbed values        		
        		listener.changed(rule, desc, newValues);
        	}
        });
    }

    @Override
    protected void update(Rule rule, PropertyDescriptor<?> desc, List<Object> newValues) {
        rule.setProperty((StringMultiProperty)desc, newValues.toArray(new String[newValues.size()]));
    }
    
    @Override
    protected Object addValueIn(Control widget, PropertyDescriptor<?> desc, Rule rule) {
        
        String newValue = ((Text)widget).getText().trim();
        if (StringUtil.isEmpty(newValue)) return null;
        
        String[] currentValues = (String[])rule.getProperty(desc);
        String[] newValues = CollectionUtil.addWithoutDuplicates(currentValues, newValue);
        if (currentValues.length == newValues.length) return null;
        
        rule.setProperty((StringMultiProperty)desc, newValues);
        return newValue;
    }
}
