import hudson.security.GlobalMatrixAuthorizationStrategy
import hudson.security.HudsonPrivateSecurityRealm
import jenkins.model.Jenkins
import jenkins.model.JenkinsLocationConfiguration
import org.jenkinsci.plugins.matrixauth.AuthorizationType
import org.jenkinsci.plugins.matrixauth.PermissionEntry


def sentinel = new File('/var/jenkins_home/.sentinel-setup')
if(!sentinel.exists()) {

    def env = System.getenv()
    def instance = Jenkins.get()

    def realm = new HudsonPrivateSecurityRealm(false, false, null)
    def user = realm.createAccount(env.JENKINS_USERNAME, env.JENKINS_PASSWORD)
    user.save()
    instance.setSecurityRealm(realm)

    def strategy = new GlobalMatrixAuthorizationStrategy()
    def permissionEntry = new PermissionEntry(AuthorizationType.USER, env.JENKINS_USERNAME)
    strategy.add(Jenkins.ADMINISTER, permissionEntry)
    instance.setAuthorizationStrategy(strategy)

    def jenkinsLocationConfiguration = instance.getDescriptorOrDie(jenkins.model.JenkinsLocationConfiguration)
    jenkinsLocationConfiguration.setUrl(env.JENKINS_URL)
    jenkinsLocationConfiguration.save()

    instance.setNumExecutors(0)

    instance.save()

    sentinel.createNewFile()

}
