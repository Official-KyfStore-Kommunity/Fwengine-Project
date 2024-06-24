using System;
using System.Diagnostics;

namespace Fwengine
{
    public class FwengineCore
    {
        public class FEConsole
        {
            public void Println(string input)
            {
                try
                {
                    // Start a new command prompt process
                    ProcessStartInfo psi = new ProcessStartInfo();
                    psi.FileName = "cmd";
                    psi.Arguments = $"/k echo {input}";

                    // Configure process settings
                    psi.UseShellExecute = true;  // Required to start a new window
                    psi.CreateNoWindow = false;  // Ensure a new window is created

                    // Log diagnostics
                    Console.WriteLine("Starting cmd process with arguments: " + psi.Arguments);
                    Console.WriteLine("Current working directory: " + Environment.CurrentDirectory);
                    
                    // Start the process
                    Process process = Process.Start(psi);
                    
                    if (process == null)
                    {
                        throw new InvalidOperationException("Process could not be started.");
                    }

                    // Wait for the process to exit
                    process.WaitForExit();
                }
                catch (Exception ex)
                {
                    // Log the error
                    Console.WriteLine("An error occurred: " + ex.Message);
                }
            }
        }
    }
}
