/*
 * Cloud Computing
 *
 * Dynamic Resource Management Tool
 * using AWS Java SDK Library
 *
 */
import java.util.Iterator;
import java.util.Scanner;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.Tag;
import java.util.UUID;

public class Main {

    static AmazonEC2 ec2;

    private static void init() {

        ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
        try {
            credentialsProvider.getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                            "Please make sure that your credentials file is at the correct " +
                            "location (~/.aws/credentials), and is in valid format.",
                    e);
        }
        ec2 = AmazonEC2ClientBuilder.standard()
                .withCredentials(credentialsProvider)
                .withRegion("ap-northeast-2")    /* check the region at AWS console */
                .build();
    }

    public static void main(String[] args) {

        init();
        Scanner menu = new Scanner(System.in);
        Scanner id_string = new Scanner(System.in);
        int number = 0;

        while(true)
        {
            System.out.println("                                                            ");
            System.out.println("                                                            ");
            System.out.println("------------------------------------------------------------");
            System.out.println("           Amazon AWS Control Panel using SDK               ");
            System.out.println("------------------------------------------------------------");
            System.out.println("  1. list instance                2. available zones        ");
            System.out.println("  3. start instance               4. available regions      ");
            System.out.println("  5. stop instance                6. create instance        ");
            System.out.println("  7. reboot instance              8. list images            ");
            System.out.println("  9. instance types               10. rename instance       ");
            System.out.println("  11. create image                12. describe instance     ");
            System.out.println("  13. describe ami                99. quit                  ");
            System.out.println("------------------------------------------------------------");

            System.out.print("Enter an integer: ");

            if(menu.hasNextInt()){
                number = menu.nextInt();
            }else {
                System.out.println("concentration!");
                break;
            }


            String instance_id = "";

            switch (number) {
                case 1 -> listInstances();
                case 2 -> availableZones();
                case 3 -> {
                    System.out.print("Enter instance id: ");
                    if (id_string.hasNext())
                        instance_id = id_string.nextLine();
                    if (!instance_id.isBlank())
                        startInstance(instance_id);
                }
                case 4 -> availableRegions();
                case 5 -> {
                    System.out.print("Enter instance id: ");
                    if (id_string.hasNext())
                        instance_id = id_string.nextLine();
                    if (!instance_id.isBlank())
                        stopInstance(instance_id);
                }
                case 6 -> {
                    System.out.print("Enter ami id: ");
                    String ami_id = "";
                    if (id_string.hasNext())
                        ami_id = id_string.nextLine();
                    if (!ami_id.isBlank())
                        createInstance(ami_id);
                }
                case 7 -> {
                    System.out.print("Enter instance id: ");
                    if (id_string.hasNext())
                        instance_id = id_string.nextLine();
                    if (!instance_id.isBlank())
                        rebootInstance(instance_id);
                }
                case 8 -> listImages();
                case 9 -> instanceType();
                case 10 -> {
                    System.out.print("Enter instance id: ");
                    if (id_string.hasNext())
                        instance_id = id_string.nextLine();
                    if (!instance_id.isBlank()) {
                        System.out.print("Enter new name: ");
                        String newName = id_string.nextLine();
                        renameInstance(instance_id, newName);
                    }

                }
                case 11 -> {
                    System.out.print("Enter instance id: ");
                    if (id_string.hasNext())
                        instance_id = id_string.nextLine();
                    if (!instance_id.isBlank()) {
                        createImage(instance_id);
                    }
                }
                case 12 -> {
                    System.out.print("Enter instance id: ");
                    if (id_string.hasNext())
                        instance_id = id_string.nextLine();
                    if (!instance_id.isBlank()) {
                        describeInstance(instance_id);
                    }
                }
                case 13 -> {
                    System.out.print("Enter AMI id: ");
                    if (id_string.hasNext())
                        instance_id = id_string.nextLine();
                    if (!instance_id.isBlank()) {
                        describeAMI(instance_id);
                    }
                }
                case 99 -> {
                    System.out.println("bye!");
                    menu.close();
                    id_string.close();
                    return;
                }
                default -> System.out.println("concentration!");
            }

        }

    }

    public static void listInstances() {
        System.out.println("Listing instances....");
        boolean done = false;

        DescribeInstancesRequest request = new DescribeInstancesRequest();

        while(!done) {
            DescribeInstancesResult response = ec2.describeInstances(request);

            for(Reservation reservation : response.getReservations()) {
                for(Instance instance : reservation.getInstances()) {
                    System.out.printf(
                            "[id] %s, " +
                                    "[AMI] %s, " +
                                    "[type] %s, " +
                                    "[state] %10s, " +
                                    "[monitoring state] %s",
                            instance.getInstanceId(),
                            instance.getImageId(),
                            instance.getInstanceType(),
                            instance.getState().getName(),
                            instance.getMonitoring().getState());
                }
                System.out.println();
            }

            request.setNextToken(response.getNextToken());

            if(response.getNextToken() == null) {
                done = true;
            }
        }
    }

    public static void availableZones()	{

        System.out.println("Available zones....");
        try {
            DescribeAvailabilityZonesResult availabilityZonesResult = ec2.describeAvailabilityZones();
            Iterator <AvailabilityZone> iterator = availabilityZonesResult.getAvailabilityZones().iterator();

            AvailabilityZone zone;
            while(iterator.hasNext()) {
                zone = iterator.next();
                System.out.printf("[id] %s,  [region] %15s, [zone] %15s\n", zone.getZoneId(), zone.getRegionName(), zone.getZoneName());
            }
            System.out.println("You have access to " + availabilityZonesResult.getAvailabilityZones().size() +
                    " Availability Zones.");

        } catch (AmazonServiceException ase) {
            System.out.println("Caught Exception: " + ase.getMessage());
            System.out.println("Reponse Status Code: " + ase.getStatusCode());
            System.out.println("Error Code: " + ase.getErrorCode());
            System.out.println("Request ID: " + ase.getRequestId());
        }

    }

    public static void startInstance(String instance_id) {

        System.out.printf("Starting .... %s\n", instance_id);
        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        DryRunSupportedRequest<StartInstancesRequest> dry_request =
                () -> {
                    StartInstancesRequest request = new StartInstancesRequest()
                            .withInstanceIds(instance_id);

                    return request.getDryRunRequest();
                };

        StartInstancesRequest request = new StartInstancesRequest()
                .withInstanceIds(instance_id);

        ec2.startInstances(request);

        System.out.printf("Successfully started instance %s", instance_id);
    }

    public static void availableRegions() {

        System.out.println("Available regions ....");

        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        DescribeRegionsResult regions_response = ec2.describeRegions();

        for(Region region : regions_response.getRegions()) {
            System.out.printf(
                    "[region] %15s, " +
                            "[endpoint] %s\n",
                    region.getRegionName(),
                    region.getEndpoint());
        }
    }

    public static void stopInstance(String instance_id) {
        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        DryRunSupportedRequest<StopInstancesRequest> dry_request =
                () -> {
                    StopInstancesRequest request = new StopInstancesRequest()
                            .withInstanceIds(instance_id);

                    return request.getDryRunRequest();
                };

        try {
            StopInstancesRequest request = new StopInstancesRequest()
                    .withInstanceIds(instance_id);

            ec2.stopInstances(request);
            System.out.printf("Successfully stop instance %s\n", instance_id);

        } catch(Exception e)
        {
            System.out.println("Exception: "+e.toString());
        }

    }

    public static void createInstance(String ami_id) {
        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        RunInstancesRequest run_request = new RunInstancesRequest()
                .withImageId(ami_id)
                .withInstanceType(InstanceType.T2Micro)
                .withMaxCount(1)
                .withMinCount(1);

        RunInstancesResult run_response = ec2.runInstances(run_request);

        String reservation_id = run_response.getReservation().getInstances().get(0).getInstanceId();

        System.out.printf(
                "Successfully started EC2 instance %s based on AMI %s",
                reservation_id, ami_id);

    }

    public static void rebootInstance(String instance_id) {

        System.out.printf("Rebooting .... %s\n", instance_id);

        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        try {
            RebootInstancesRequest request = new RebootInstancesRequest()
                    .withInstanceIds(instance_id);

            RebootInstancesResult response = ec2.rebootInstances(request);

            System.out.printf(
                    "Successfully rebooted instance %s", instance_id);

        } catch(Exception e)
        {
            System.out.println("Exception: "+e.toString());
        }


    }

    public static void listImages() {
        System.out.println("Listing images....");

        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        DescribeImagesRequest request = new DescribeImagesRequest();
        ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();

        String accountId = ec2.describeSecurityGroups().getSecurityGroups().get(0).getOwnerId();
        request.getFilters().add(new Filter().withName("owner-id").withValues(accountId));

        request.setRequestCredentialsProvider(credentialsProvider);

        DescribeImagesResult results = ec2.describeImages(request);

        for(Image images :results.getImages()){
            System.out.printf("[ImageID] %s, [Name] %s, [Owner] %s\n",
                    images.getImageId(), images.getName(), images.getOwnerId());
        }

    }

    public static void instanceType() {
        int idx = 1;
        System.out.println("Listing available instance types");
        for (InstanceType instanceType : InstanceType.values()) {
            System.out.println(idx+". "+instanceType.toString());
            idx += 1;
        }
    }

    public static void renameInstance(String instance_id, String newName) {
        System.out.println("Changing instance name....");
        Tag tag = new Tag().withKey("Name").withValue(newName);
        CreateTagsRequest createTagsRequest = new CreateTagsRequest()
                .withResources(instance_id)
                .withTags(tag);

        ec2.createTags(createTagsRequest);

        System.out.println("The instance name has been changed to "+newName+".");
    }

    public static void createImage(String instance_id) {
        System.out.println("Creating an AMI (Amazon Machine Image)....");
        String imageName = "image_" + UUID.randomUUID();
        CreateImageRequest createImageRequest = new CreateImageRequest()
                .withInstanceId(instance_id)
                .withName(imageName)
                .withNoReboot(true);

        CreateImageResult createImageResult = ec2.createImage(createImageRequest);
        String imageId = createImageResult.getImageId();

        System.out.println("image "+ imageId+" has been created." );
    }

    public static void describeInstance(String instance_id) {
        DescribeInstancesRequest describeInstancesRequest = new DescribeInstancesRequest()
                .withInstanceIds(instance_id);

        DescribeInstancesResult describeInstancesResult = ec2.describeInstances(describeInstancesRequest);

        System.out.println("Describing instance....");
        for (Reservation reservation : describeInstancesResult.getReservations()) {
            for (Instance instance : reservation.getInstances()) {
                System.out.println("Instance tag: " + instance.getTags().getFirst().getValue());
                System.out.println("Instance type: " + instance.getInstanceType());
                System.out.println("Public DNS Name: " + instance.getPublicDnsName());
                System.out.println("Private DNS Name: " + instance.getPrivateDnsName());
                System.out.println("State: " + instance.getState().getName());
                System.out.println("Public Ip address: " + instance.getPublicIpAddress());
                System.out.println("Private Ip address: " + instance.getPrivateIpAddress());
            }
        }
    }

    public static void describeAMI(String ami_id) {

        DescribeImagesRequest describeImagesRequest = new DescribeImagesRequest()
                .withImageIds(ami_id);

        DescribeImagesResult describeImagesResult = ec2.describeImages(describeImagesRequest);

        System.out.println("Describing an AMI (Amazon Machine Image)....");
        for (Image image : describeImagesResult.getImages()) {
            System.out.println("Name: " + image.getName());
            System.out.println("Creation date: " + image.getCreationDate());
            System.out.println("Owner: " + image.getOwnerId());
            System.out.println("State: " + image.getState());
            System.out.println("Architecture: " + image.getArchitecture());
        }
    }
}