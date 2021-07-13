package neu.lab.dependency.util;

import org.apache.maven.artifact.manager.WagonConfigurationException;
import org.apache.maven.artifact.manager.WagonManager;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Settings;
import org.apache.maven.wagon.ConnectionException;
import org.apache.maven.wagon.UnsupportedProtocolException;
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.observers.Debug;
import org.apache.maven.wagon.proxy.ProxyInfo;
import org.apache.maven.wagon.repository.Repository;

/**
 * Utility methods to help with using {@link Wagon}s.
 *
 * @author SUNJUNYAN
 */
public final class WagonUtils {
    private WagonUtils() {
        throw new IllegalAccessError("Utility classes should never be instantiated");
    }

    /**
     * Convenience method to convert the {@link Proxy} object from a
     * {@link Settings} into a {@link ProxyInfo}.
     *
     * @param settings The settings to use.
     * @return The proxy details from the settings or <code>null</code> if the settings do not define a proxy.
     */
    public static ProxyInfo getProxyInfo(Settings settings) {
        ProxyInfo proxyInfo = null;
        if (settings != null && settings.getActiveProxy() != null) {
            proxyInfo = new ProxyInfo();
            final Proxy proxy = settings.getActiveProxy();
            proxyInfo.setHost(proxy.getHost());
            proxyInfo.setType(proxy.getProtocol());
            proxyInfo.setPort(proxy.getPort());
            proxyInfo.setNonProxyHosts(proxy.getNonProxyHosts());
            proxyInfo.setUserName(proxy.getUsername());
            proxyInfo.setPassword(proxy.getPassword());
        }
        return proxyInfo;
    }

    /**
     * Convenience method to create a wagon.
     *
     * @param serverId     The serverId to use if the wagonManager needs help.
     * @param url          The url to create a wagon for.
     * @param wagonManager The wgaon manager to use.
     * @param settings     The settings to use.
     * @param logger       The logger to use.
     * @return The wagon to connect to the url.
     * @throws UnsupportedProtocolException if the protocol is not supported.
     * @throws WagonConfigurationException  if the wagon cannot be configured.
     * @throws ConnectionException          If the connection cannot be established.
     * @throws AuthenticationException      If the connection cannot be authenticated.
     */
    public static Wagon createWagon(String serverId, String url, WagonManager wagonManager, Settings settings,
                                    Log logger)
            throws UnsupportedProtocolException, WagonConfigurationException,
            ConnectionException, AuthenticationException {
        Repository repository = new Repository(serverId, url);
        Wagon wagon = wagonManager.getWagon(repository);

        if (logger.isDebugEnabled()) {
            Debug debug = new Debug();
            wagon.addSessionListener(debug);
            wagon.addTransferListener(debug);
        }

        ProxyInfo proxyInfo = getProxyInfo(settings);
        if (proxyInfo != null) {
            wagon.connect(repository, wagonManager.getAuthenticationInfo(repository.getId()), proxyInfo);
        } else {
            wagon.connect(repository, wagonManager.getAuthenticationInfo(repository.getId()));
        }
        return wagon;
    }
}
