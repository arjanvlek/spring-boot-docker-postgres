package nl._42.boot.docker.postgres.containers;

import nl._42.boot.docker.postgres.shared.DockerHeaderMismatch;
import nl._42.boot.docker.postgres.shared.DockerInformation;
import nl._42.boot.docker.postgres.shared.DockerListHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class DockerContainerInformation extends DockerInformation<DockerContainer> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DockerContainerInformation.class);

    public DockerContainerInformation(String[] lines) throws DockerHeaderMismatch {
        super(lines);
        checkForMissingPorts();
    }

    private void checkForMissingPorts() {
        for (DockerContainer container : getList()) {
            // Implies no port, names are in the port column
            if (container.getNames() == null) container.repairIfNameInPort();
        }
    }

    @Override
    protected DockerListHeaders checkHeaders(String[] headers) {
        return new DockerContainerListHeaders(headers);
    }

    @Override
    protected DockerContainer createLine(Map<Integer, Integer> actualToIntendedMapping) {
        return new DockerContainer(actualToIntendedMapping);
    }

    public boolean hasContainer(String containerName) {
        for (DockerContainer container : getList()) {
            if (containerName.equals(container.getNames())) {
                LOGGER.info("| Container [" + containerName + "] exists");
                return true;
            }
        }
        LOGGER.info("| Container [" + containerName + "] not found; no force remove needed");
        return false;
    }

    public String portOccupied(Integer port) {
        for (DockerContainer container : getList()) {
            if (container.portActivelyOccupied(port)) {
                return container.getNames();
            }
        }
        return null;
    }

}
