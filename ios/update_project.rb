#!/usr/bin/ruby
# -*- coding: UTF-8 -*-
require 'xcodeproj'

project_path = "iOS/OneKey.xcodeproj";

# Create project object
project = Xcodeproj::Project.open(project_path);


target = project.targets[0]
build_phase = target.frameworks_build_phase
build_phase.files_references.each do |pbx_build_file_ref|
   build_phase.remove_file_reference(pbx_build_file_ref) if pbx_build_file_ref.display_name.start_with?("_", "lru")
end

target.build_configurations.each do |config|
    if config.name == 'Release'
        config.build_settings["Strip Debug Symbols During Copy"] =  "NO"
        config.build_settings["Enable Bitcode"] =  "NO"
        config.build_settings["Strip Linked Product"] = "NO";
        config.build_settings["Strip Style"] = "Debugging Symbols";
        config.build_settings["Symbols Hidden by Default"] = "NO";
        config.build_settings["Valid Architectures"] = "arm64";
    end
end

# Save the project
project.save();


