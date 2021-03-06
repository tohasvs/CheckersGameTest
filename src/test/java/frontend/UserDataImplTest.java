package frontend;

import base.DataAccessObject;
import base.GameMechanic;
import base.MessageSystem;
import base.UserData;
import dbService.DBServiceImpl;
import dbService.UserDataSet;
import gameMechanic.GameMechanicImpl;
import messageSystem.MessageSystemImpl;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.mockito.Matchers;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import resource.TimeSettings;
import utils.TimeHelper;

import static org.mockito.Mockito.*;

public class UserDataImplTest {

    public static final String FAKE_SESSION_ID = "fake_session_id";
    public static final int FAKE_USER_ID = 100;
    public static final String SESSION_ID_1 = "session_id_1";
    public static final String SESSION_ID_2 = "session_id_2";
    public static final int USER_ID_1 = 100;
    public static final int USER_ID_2 = 101;
    private UserDataImpl userData;
    private MessageSystem messageSystem;
    private DataAccessObject dbService;
    private GameMechanic gameMechanic;

    @BeforeMethod
    public void setUp() throws Exception {
        messageSystem = spy(new MessageSystemImpl());
        dbService = new DBServiceImpl(messageSystem);
        userData = new UserDataImpl(messageSystem);
        gameMechanic = new GameMechanicImpl(messageSystem);
    }

    @Test
    public void testGetSession() throws Exception {
        UserDataImpl.putLogInUser(FAKE_SESSION_ID, new UserDataSet(FAKE_USER_ID, "FAKE_NAME", 0, 0, 0));
        Assert.assertEquals(UserDataImpl.getSessionIdByUserId(FAKE_USER_ID), FAKE_SESSION_ID);
    }

    @Test
    public void testGetSessionByUserId() throws Exception {

        UserDataSet userDataSet1 = new UserDataSet(USER_ID_1, "nick1", 0, 0, 0);
        UserDataSet userDataSet2 = new UserDataSet(USER_ID_2, "nick2", 0, 0, 0);
        UserDataImpl.putLogInUser(SESSION_ID_1, userDataSet1);
        UserDataImpl.putLogInUser(SESSION_ID_2, userDataSet2);

        Assert.assertNull(UserDataImpl.getSessionIdByUserId(100500));
    }

    @Test
    public void testUpdateUserId() throws Exception {
        UserDataSet userDataSet1 = new UserDataSet(USER_ID_1, "nick1", 0, 0, 0);
        UserDataImpl.putSessionIdAndUserSession(SESSION_ID_1, userDataSet1);

        userData.updateUserId(SESSION_ID_1, null);
        Assert.assertEquals(UserDataImpl.getUserSessionBySessionId(SESSION_ID_1).getPostStatus(), 0);

        userData.updateUserId(SESSION_ID_1, userDataSet1);

        UserDataImpl.putLogInUser(SESSION_ID_1, userDataSet1);
        UserDataImpl.putSessionIdAndUserSession(SESSION_ID_2, userDataSet1);
        userData.updateUserId(SESSION_ID_2, userDataSet1);
        Assert.assertTrue(messageSystem.getMessages().get(messageSystem.getAddressByName(GameMechanicImpl.SERVICE_NAME)).size() > 0);
    }

    @Test
    public void testPartyEndNULL() throws Exception {
        UserDataSet userDataSet1 = new UserDataSet(USER_ID_1, "nick1", 0, 0, 0);
        UserDataSet userDataSet2 = new UserDataSet(USER_ID_2, "nick2", 0, 0, 0);

        UserDataImpl.putLogInUser(SESSION_ID_1, userDataSet1);
        UserDataImpl.putLogInUser(SESSION_ID_2, userDataSet2);

        userData.partyEnd(USER_ID_1, USER_ID_2);
    }

    @Test
    public void testPartyEndRaitingEquals() throws Exception {
        UserDataSet userDataSet1 = new UserDataSet(USER_ID_1, "nick1", 0, 0, 0);
        UserDataSet userDataSet2 = new UserDataSet(USER_ID_2, "nick2", 0, 0, 0);

        UserDataImpl.putLogInUser(SESSION_ID_1, userDataSet1);
        UserDataImpl.putLogInUser(SESSION_ID_2, userDataSet2);

        UserDataImpl.putSessionIdAndUserSession(SESSION_ID_1, userDataSet1);
        UserDataImpl.putSessionIdAndUserSession(SESSION_ID_2, userDataSet2);
        userData.partyEnd(USER_ID_1, USER_ID_2);
    }

    @Test
    public void testPartyEnd() throws Exception {
        UserDataSet userDataSet1 = new UserDataSet(USER_ID_1, "nick1", 50, 0, 0);
        UserDataSet userDataSet2 = new UserDataSet(USER_ID_2, "nick2", 30, 0, 0);

        UserDataImpl.putLogInUser(SESSION_ID_1, userDataSet1);
        UserDataImpl.putLogInUser(SESSION_ID_2, userDataSet2);

        UserDataImpl.putSessionIdAndUserSession(SESSION_ID_1, userDataSet1);
        UserDataImpl.putSessionIdAndUserSession(SESSION_ID_2, userDataSet2);
        userData.partyEnd(USER_ID_1, USER_ID_2);
        Assert.assertTrue(messageSystem.getMessages().get(messageSystem.getAddressByName(DBServiceImpl.SERVICE_NAME)).size() > 0);
    }


/*    @Test
    public void testKeepALive() throws Exception {
        WebSocketImpl webSocket = mock(WebSocketImpl.class);
        Session session = mock(Session.class);
        RemoteEndpoint remoteEndpoint = mock(RemoteEndpoint.class);
        when(webSocket.getSession()).thenReturn(session);
        when(session.getRemote()).thenReturn(remoteEndpoint);

        UserDataSet userDataSet = new UserDataSet();

        UserDataImpl.putSessionIdAndWS(SESSION_ID_1, webSocket);
        UserDataImpl.putSessionIdAndUserSession(SESSION_ID_1, userDataSet);
        UserDataImpl.putLogInUser(SESSION_ID_1, userDataSet);

        //new Thread(userData).start();
        TimeHelper.sleep(5000);
    }*/

/*    @Test
    public void testKeepALiveTime() throws Exception {
        TimeSettings.setExitTime(10000000);
        WebSocketImpl webSocket = mock(WebSocketImpl.class);
        Session session = mock(Session.class);
        RemoteEndpoint remoteEndpoint = mock(RemoteEndpoint.class);
        when(webSocket.getSession()).thenReturn(session);
        when(session.getRemote()).thenReturn(remoteEndpoint);

        UserDataSet userDataSet = new UserDataSet();

        UserDataImpl.putSessionIdAndWS(SESSION_ID_1, webSocket);
        UserDataImpl.putSessionIdAndUserSession(SESSION_ID_1, userDataSet);
        UserDataImpl.putLogInUser(SESSION_ID_1, userDataSet);

        //new Thread(userData).start();
        TimeHelper.sleep(500);
        verify(UserDataImpl.getWSBySessionId(SESSION_ID_1), times(1)).sendString(Matchers.eq("1"));
    }*/

}
