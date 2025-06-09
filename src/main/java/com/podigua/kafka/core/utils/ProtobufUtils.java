package com.podigua.kafka.core.utils;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProtobufUtils {
    public static void main(String[] args) {
        List<Descriptors.Descriptor> descriptors = descriptors(new File("/Users/podigua/Desktop/BaseEvent.description"));
        System.out.println(descriptors);
    }
    public static List<Descriptors.Descriptor> descriptors(File file) {
        try {
            DescriptorProtos.FileDescriptorSet fileDescriptorSet = DescriptorProtos.FileDescriptorSet.parseFrom(new FileInputStream(file));
            Map<String, Descriptors.FileDescriptor> fileDescriptorMap = buildFileDescriptors(fileDescriptorSet);
            List<Descriptors.Descriptor> allMessageDescriptors = new ArrayList<>();
            for (Descriptors.FileDescriptor fd : fileDescriptorMap.values()) {
                collectAllMessageTypes(fd, allMessageDescriptors);
            }
            return allMessageDescriptors;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void collectAllMessageTypes(Descriptors.FileDescriptor fileDescriptor, List<Descriptors.Descriptor> result) {
        for (Descriptors.Descriptor topLevelType : fileDescriptor.getMessageTypes()) {
            collectNestedMessageTypes(topLevelType, result);
        }
    }

    private static void collectNestedMessageTypes(Descriptors.Descriptor descriptor, List<Descriptors.Descriptor> result) {
        result.add(descriptor);
        for (Descriptors.Descriptor nestedType : descriptor.getNestedTypes()) {
            collectNestedMessageTypes(nestedType, result);
        }
    }

    private static Map<String, Descriptors.FileDescriptor> buildFileDescriptors(DescriptorProtos.FileDescriptorSet fileDescriptorSet) throws Exception {
        Map<String, DescriptorProtos.FileDescriptorProto> protoByName = new HashMap<>();
        List<DescriptorProtos.FileDescriptorProto> protoList = fileDescriptorSet.getFileList();
        for (DescriptorProtos.FileDescriptorProto proto : protoList) {
            protoByName.put(proto.getName(), proto);
        }

        Map<String, Descriptors.FileDescriptor> fileDescriptorMap = new HashMap<>();
        for (DescriptorProtos.FileDescriptorProto proto : protoList) {
            buildFileDescriptor(proto, protoByName, fileDescriptorMap);
        }

        return fileDescriptorMap;
    }

    private static Descriptors.FileDescriptor buildFileDescriptor(
            DescriptorProtos.FileDescriptorProto proto,
            Map<String, DescriptorProtos.FileDescriptorProto> protoByName,
            Map<String, Descriptors.FileDescriptor> fileDescriptorMap) throws Exception {
        if (fileDescriptorMap.containsKey(proto.getName())) {
            return fileDescriptorMap.get(proto.getName());
        }

        List<Descriptors.FileDescriptor> dependencies = new ArrayList<>();
        for (String depName : proto.getDependencyList()) {
            DescriptorProtos.FileDescriptorProto depProto = protoByName.get(depName);
            if (depProto == null) {
                throw new RuntimeException("找不到依赖的 proto 文件: " + depName);
            }
            Descriptors.FileDescriptor depDescriptor = buildFileDescriptor(depProto, protoByName, fileDescriptorMap);
            dependencies.add(depDescriptor);
        }

        Descriptors.FileDescriptor descriptor = Descriptors.FileDescriptor.buildFrom(proto, dependencies.toArray(new Descriptors.FileDescriptor[0]));
        fileDescriptorMap.put(proto.getName(), descriptor);
        return descriptor;
    }
}
