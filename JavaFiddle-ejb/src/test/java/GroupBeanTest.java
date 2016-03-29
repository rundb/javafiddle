import org.junit.*;
import org.junit.Test;
import ru.javafiddle.core.ejb.*;
import ru.javafiddle.jpa.entity.*;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by mac on 14.03.16.
 */
public class GroupBeanTest {


    private static final Logger logger =
            Logger.getLogger(ProjectBean.class.getName());

    EJBContainer ejbContainer;
    Context context;
    @Before
    public void setUp() {
        Map<String, Object> properties = new HashMap<String, Object>();
        //properties.put(EJBContainer.MODULES, new File("target/classes");
        properties.put("org.glassfish.ejb.embedded.glassfish.installation.root",
                "/Users/mac/glassfish4/glassfish");
        ejbContainer = EJBContainer.createEJBContainer();
        System.out.println("Test EJBContainer is created");
        context = ejbContainer.getContext();
    }

    @After
    public void tearDown() {
        ejbContainer.close();
        System.out.println("Test EJBContainer is closed" );
    }
//Some small functions are used in these three or four main ones.

    @Test
    public void testGroupOperations() throws UnsupportedEncodingException, NoSuchAlgorithmException, InstantiationException, IllegalAccessException {

        GroupBean groupBean = null;
        ProjectBean projectBean = null;
        UserBean userBean = null;
        AccessBean accessBean = null;
        UserGroupBean userGroupBean = null;

        try {
            groupBean = (GroupBean) context.lookup("java:global/JavaFiddle-ejb/GroupBean");
            projectBean = (ProjectBean) context.lookup("java:global/JavaFiddle-ejb/ProjectBean");
            userBean = (UserBean) context.lookup("java:global/JavaFiddle-ejb/UserBean");
            accessBean = (AccessBean) context.lookup("java:global/JavaFiddle-ejb/AccessBean");
            userGroupBean = (UserGroupBean) context.lookup("java:global/JavaFiddle-ejb/UserGroupBean");
        } catch (NamingException ex) {
            System.out.println("Unable to initialize UserBean instance: " + ex);
        }



        initializeAndCreate(groupBean, projectBean,userBean, accessBean);
        int groupId = groupBean.getGroupByGroupId(1).getGroupId();
      //  userGroupBean.getUserGroup(1,1);
//added member-----------------------------------------------------------
        Group group = groupBean.getGroupByGroupId(1);
        User user1 = userBean.getUser("barny");
        User user2 = userBean.getUser("uollis");
        Access access1 = accessBean.getAccess("partial");
        Access access2 = accessBean.getAccess("full");
        groupBean.addMember(group,user1,access1);
        groupBean.addMember(group,user2,access2);
//check if member was added----------------------------------------------
        group = groupBean.getGroupByGroupId(1);
        Assert.assertNotNull(group);
        System.out.println(group.getGroupName());
        List<UserGroup> gr = groupBean.getGroupMembers(1);

        Assert.assertNotNull("GET MEMBERS RETURNED NULL",gr);
        Assert.assertFalse("THE LIST OF MEMBERS IS EMPTY", gr.isEmpty());
        if (gr == null || gr.isEmpty()) {

            logger.log(Level.SEVERE, "NO ENTITY WAS ADDED");
            System.exit(-1);

        }
        for (UserGroup ug:gr) {
            System.out.println(ug.getMember().getFirstName());
        }


//check getAllMembers()----------------------------------------------------------------
        Assert.assertEquals("NOT ENOUGH MEMBERS IN MAP", 2,groupBean.getMemberAccessMap(groupId).size());
        for (Map.Entry<String, String> entry : groupBean.getMemberAccessMap(groupId).entrySet()) {
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }



//Delete operation--------------------------------------------------------
        groupBean.deleteMember(groupId,"barny");
        groupBean.deleteMember(groupId, "uollis");
        //check if was deleted
        group = groupBean.getGroupByGroupId(1);
        gr = groupBean.getGroupMembers(group.getGroupId());
        Assert.assertTrue("ERROR", gr.isEmpty());

    }

    public void initializeAndCreate(GroupBean groupBean, ProjectBean projectBean, UserBean userBean, AccessBean accessBean) {

        Group group = new Group("extended");
        Group g = groupBean.createGroup(group);
        User user1 = new User("Oleg", "Ruzh", "barny", "aa", "gfhkfo", null, null);
        User uu = userBean.register(user1);
        User user2 = new User("Vadim", "Vet", "uollis", "gg", "frewscfs", null, null);
        User uu2 = userBean.register(user2);

        Access access = new Access("partial");
        Access a = accessBean.createAccess(access);
        Access access2 = new Access("full");
        Access a2 = accessBean.createAccess(access2);



    }
}

